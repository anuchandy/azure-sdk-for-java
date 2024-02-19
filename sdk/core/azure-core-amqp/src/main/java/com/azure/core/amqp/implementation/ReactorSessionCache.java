// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

import com.azure.core.amqp.exception.AmqpException;
import com.azure.core.util.logging.ClientLogger;
import org.apache.qpid.proton.engine.Connection;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static com.azure.core.amqp.exception.AmqpErrorCondition.TIMEOUT_ERROR;
import static com.azure.core.amqp.implementation.ClientConstants.SESSION_NAME_KEY;

final class ReactorSessionCache {
    private final ConcurrentMap<String, Entry> entries = new ConcurrentHashMap<>();
    private final String fullyQualifiedNamespace;
    private final String connectionId;
    private final ReactorHandlerProvider handlerProvider;
    private final ReactorProvider reactorProvider;
    private final Duration openTimeout;
    private final AtomicBoolean isOwnerDisposed;
    private final ClientLogger logger;

    ReactorSessionCache(String fullyQualifiedNamespace, String connectionId, ReactorHandlerProvider handlerProvider,
        ReactorProvider reactorProvider, Duration openTimeout, ClientLogger logger) {
        this.fullyQualifiedNamespace = fullyQualifiedNamespace;
        this.connectionId = connectionId;
        this.handlerProvider = handlerProvider;
        this.reactorProvider = reactorProvider;
        this.openTimeout = openTimeout;
        this.isOwnerDisposed = new AtomicBoolean(false);
        this.logger = logger;
    }

    void ownerDisposed() {
        isOwnerDisposed.set(true);
    }

    Mono<ReactorSession> getOrLoad(Mono<Connection> connectionMono, String name,
        Function<ProtonSessionWrapper, ReactorSession> cacheLoader) {
        return connectionMono.map(connection -> {
            final Entry cached = entries.computeIfAbsent(name, k -> load(connection, k, cacheLoader));
            return cached;
        }).flatMap(cached -> {
            return cached.openSession()
                .doOnError(error -> {
                    if (!(error instanceof AmqpException)) {
                        return;
                    }
                    // Clean up the subscription if there was an error while waiting for session to active.
                    final AmqpException amqpException = (AmqpException) error;
                    if (amqpException.getErrorCondition() == TIMEOUT_ERROR) {
                        remove(name);
                    }
                });
        });
    }

    boolean remove(String name) {
        if (name == null) {
            return false;
        }
        final Entry removed = entries.remove(name);
        if (removed != null) {
            removed.dispose();
        }
        return removed != null;
    }

    Mono<Void> awaitClose() {
        final ArrayList<Mono<Void>> closing = new ArrayList<>(entries.size());
        for (Entry entry : entries.values()) {
            closing.add(entry.isSessionClosed());
        }
        return Mono.when(closing);
    }

    private Entry load(Connection connection, String name,
        Function<ProtonSessionWrapper, ReactorSession> cacheLoader) {
        final ReactorSession session = cacheLoader.apply(protonSession(connection, name));
        final Disposable disposable = session.getEndpointStates()
            .subscribe(__ -> {
            }, error -> {
                if (isOwnerDisposed.get()) {
                    // If (owning) connection is already disposing of, all session(s) would be discarded,
                    // avoid double-close call and close Subscription allocation.
                    return;
                }
                logger.atInfo()
                    .addKeyValue(SESSION_NAME_KEY, name)
                    .log("Removing session terminated with error.", error);
                remove(name);
            }, () -> {
                if (isOwnerDisposed.get()) {
                    return;
                }
                remove(name);
            });
        return new Entry(session, disposable);
    }

    private ProtonSessionWrapper protonSession(Connection connection, String name) {
        final ProtonSession protonSession = new ProtonSession(fullyQualifiedNamespace, connectionId, connection,
            handlerProvider, reactorProvider, name, openTimeout, logger);
        return new ProtonSessionWrapper(protonSession);
    }

    private static final class Entry extends AtomicBoolean {
        private final ReactorSession session;
        private final Disposable disposable;

        private Entry(ReactorSession session, Disposable disposable) {
            super(false);
            this.session = session;
            this.disposable = disposable;
        }

        private Mono<ReactorSession> openSession() {
            return session.open().thenReturn(session);
        }

        private Mono<Void> isSessionClosed() {
            return session.isClosed();
        }

        private void dispose() {
            if (super.getAndSet(true)) {
                return;
            }
            session.closeAsync("closing session.", null, true)
                .subscribe();
            disposable.dispose();
        }
    }
}
