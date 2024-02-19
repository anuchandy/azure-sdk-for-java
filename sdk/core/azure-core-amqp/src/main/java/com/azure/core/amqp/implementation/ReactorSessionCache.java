// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

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

import static com.azure.core.amqp.implementation.ClientConstants.SESSION_NAME_KEY;

/**
 * A cache for {@link ReactorSession} hosted on a QPid proton-j connection, owned by a {@link ReactorConnection}.
 */
final class ReactorSessionCache {
    private final ConcurrentMap<String, Entry> entries = new ConcurrentHashMap<>();
    private final String fullyQualifiedNamespace;
    private final String connectionId;
    private final ReactorHandlerProvider handlerProvider;
    private final ReactorProvider reactorProvider;
    private final Duration openTimeout;
    private final AtomicBoolean isOwnerDisposed;
    private final ClientLogger logger;

    /**
     * Creates the cache.
     *
     * @param fullyQualifiedNamespace the host name of the broker.
     * @param connectionId the id of the {@link ReactorConnection} hosting the sessions in the cache.
     * @param handlerProvider the handler provider for various type of endpoints.
     * @param reactorProvider the provider for reactor dispatcher.
     * @param openTimeout the session open timeout.
     * @param logger the client logger.
     */
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

    void setOwnerDisposed() {
        isOwnerDisposed.set(true);
    }

    /**
     * Obtain the session with the given name from the cache, first loading and opening the session if necessary.
     * <p>
     * The session returned from the cache will be already connected to the broker and ready to use.
     * </p>
     * <p>
     * A session will be evicted from the cache if it terminates (e.g., broker disconnected the session).
     * </p>
     *
     * @param connectionMono the Mono that emits QPid proton-j connection that host the session.
     * @param name the session name.
     * @param loader the function to load the session on cache miss, cache miss can happen if session is requested
     *  for the first time or previously loaded one was evicted.
     *
     * @return the session, that is active and connected to the broker.
     */
    Mono<ReactorSession> getOrLoad(Mono<Connection> connectionMono, String name, Function<ProtonSessionWrapper, ReactorSession> loader) {
        final Mono<Entry> cachedMono = connectionMono.map(connection -> {
            final Entry cached = entries.computeIfAbsent(name, sessionName -> {
                final ReactorSession session = load(connection, sessionName, loader);
                final Disposable disposable = setupEviction(name, session);
                return new Entry(session, disposable);
            });
            return cached;
        });
        return cachedMono.flatMap(cached -> {
            // 'openSession()' will open the session if it is just loaded, once opened, future calls to 'openSession'
            // returns the session as long as it is connected to the broker (iow active).
            return cached.openSession()
                .doOnError(error -> evictOnError(name, "Evicting session failed to open or that didn't active.", error));
        });
    }

    /**
     * Evicts the session from the cache.
     *
     * @param name the name of the session to evict.
     * @return true if the session was evicted, false if no session found with the given name.
     */
    boolean evict(String name) {
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
            closing.add(entry.awaitSessionClose());
        }
        return Mono.when(closing);
    }

    /**
     * Load a new {@link ReactorSession} to be cached.
     *
     * @param connection the QPid proton-j connection to host the session.
     * @param name the session name.
     * @param loader the function to load the session.
     *
     * @return the session to cache.
     */
    private ReactorSession load(Connection connection, String name, Function<ProtonSessionWrapper, ReactorSession> loader) {
        final ProtonSession protonSession = new ProtonSession(fullyQualifiedNamespace, connectionId, connection,
            handlerProvider, reactorProvider, name, openTimeout, logger);
        final ReactorSession session = loader.apply(new ProtonSessionWrapper(protonSession));
        return session;
    }

    /**
     * Register to evict the session from the cache when the session terminates.
     *
     * @param name the name of the session.
     * @param session the session to register for cache eviction.
     * @return the registration disposable.
     */
    private Disposable setupEviction(String name, ReactorSession session) {
        return session.getEndpointStates()
            .subscribe(__ -> {
            }, error -> {
                evictOnError(name, "Evicting session terminated with error.", error);
            }, () -> {
                if (isOwnerDisposed.get()) {
                    return;
                }
                evict(name);
            });
    }

    /**
     * Evicts the session from the cache due to an error.
     *
     * @param name the session name.
     * @param message the message to log on eviction.
     * @param error the error triggered the eviction.
     */
    private void evictOnError(String name, String message, Throwable error) {
        if (isOwnerDisposed.get()) {
            // If (owning) connection is already disposing of, all session(s) would be discarded,
            // avoid double-close call and close Subscription allocation.
            return;
        }
        logger.atInfo().addKeyValue(SESSION_NAME_KEY, name).log(message, error);
        evict(name);
    }

    /**
     * An entry in the cache holding {@link ReactorSession} and disposable for the task to evict the entry
     * from the cache.
     */
    private static final class Entry extends AtomicBoolean {
        private final ReactorSession session;
        private final Disposable disposable;

        private Entry(ReactorSession session, Disposable disposable) {
            super(false);
            this.session = session;
            this.disposable = disposable;
        }

        /**
         * Opens the session.
         *
         * @return a Mono that completes when the session is opened.
         */
        private Mono<ReactorSession> openSession() {
            return session.open().thenReturn(session);
        }

        /**
         * Await for the session to close.
         *
         * @return a Mono that completes when the session is closed.
         */
        private Mono<Void> awaitSessionClose() {
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
