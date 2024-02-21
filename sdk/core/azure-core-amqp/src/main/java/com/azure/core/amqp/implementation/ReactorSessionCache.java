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
 * A cache of {@link ReactorSession} owned by a {@link ReactorConnection}.
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
     * @param connectionId the id of the {@link ReactorConnection} owning the cache.
     * @param fullyQualifiedNamespace the host name of the broker that the owner is connected to.
     * @param handlerProvider the handler provider for various type of endpoints (session, link).
     * @param reactorProvider the provider for reactor dispatcher.
     * @param openTimeout the session open timeout.
     * @param logger the client logger.
     */
    ReactorSessionCache(String connectionId, String fullyQualifiedNamespace, ReactorHandlerProvider handlerProvider,
        ReactorProvider reactorProvider, Duration openTimeout, ClientLogger logger) {
        this.fullyQualifiedNamespace = fullyQualifiedNamespace;
        this.connectionId = connectionId;
        this.handlerProvider = handlerProvider;
        this.reactorProvider = reactorProvider;
        this.openTimeout = openTimeout;
        this.isOwnerDisposed = new AtomicBoolean(false);
        this.logger = logger;
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
     * @param connectionMono the Mono that emits QPid Proton-j {@link Connection} that host the session.
     * @param name the session name.
     * @param loader the function to load the session on cache miss, cache miss can happen if session is requested
     *  for the first time or previously loaded one was evicted.
     *
     * @return the session, that is active and connected to the broker.
     */
    Mono<ReactorSession> getOrLoad(Mono<Connection> connectionMono, String name,
        Function<ProtonSessionWrapper, ReactorSession> loader) {
        // TODO (anu): loader: remove 'ProtonSessionWrapper' and use 'ProtonSession' directly when v1 support is removed.
        final Mono<Entry> cachedMono = connectionMono.map(connection -> {
            return entries.computeIfAbsent(name, sessionName -> {
                final ReactorSession session = load(connection, sessionName, loader);
                final Disposable disposable = setupAutoEviction(session);
                return new Entry(session, disposable);
            });
        });
        return cachedMono.flatMap(cached -> {
            final ReactorSession session = cached.getSession();
            return session.open()
                .doOnError(error -> evict(session, "Evicting failed to open or in-active session.", error));
            // 'ReactorSession::open()' has open-only-once semantics, i.e., the session open attempt is triggered upon
            // the first subscription that loads session into the cache. Later subscriptions only trigger the session
            // active check (i.e., checks if the broker connection is still active), if not, an error will be returned
            // to evict from cache.
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

    /**
     * Signal that the owner ({@link ReactorConnection}) of the cache is disposed of.
     */
    void setOwnerDisposed() {
        isOwnerDisposed.set(true);
    }

    /**
     * When the owner {@link ReactorConnection} is being disposed of, all {@link ReactorSession} loaded into the cache
     * will receive shutdown signal, the owner may use this method to waits for sessions to complete it closing.
     *
     * @return a Mono that completes when all sessions are closed via owner shutdown signaling.
     */
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
     * @param connection the QPid Proton-j connection to host the session.
     * @param name the session name.
     * @param loader the function to load the session.
     *
     * @return the session to cache.
     */
    private ReactorSession load(Connection connection, String name,
        Function<ProtonSessionWrapper, ReactorSession> loader) {
        final ProtonSession protonSession = new ProtonSession(connectionId, fullyQualifiedNamespace, connection,
            handlerProvider, reactorProvider, name, openTimeout, logger);
        // TODO (anu): Remove 'ProtonSessionWrapper' and use 'ProtonSession' directly when v1 support is removed.
        return loader.apply(new ProtonSessionWrapper(protonSession));
    }

    /**
     * Register to evict the session from the cache when the session terminates.
     *
     * @param session the session to register for cache eviction.
     * @return the registration disposable.
     */
    private Disposable setupAutoEviction(ReactorSession session) {
        return session.getEndpointStates()
            .subscribe(__ -> {
            }, error -> {
                evict(session, "Evicting session terminated with error.", error);
            }, () -> {
                evict(session, "Evicting terminated session.", null);
            });
    }

    /**
     * Attempt to evict the session from the cache.
     *
     * @param session the session to evict.
     * @param message the message to log on eviction.
     * @param error the error triggered the eviction.
     */
    private void evict(ReactorSession session, String message, Throwable error) {
        if (isOwnerDisposed.get()) {
            // If (owner) connection is already disposing of, all session(s) would be discarded. Which means the whole
            // cache itself would be discarded. Don’t evict individual entries, this avoids double close, subscription
            // allocations and prevent downstream attempting to load new sessions while connection cleanup is running.
            return;
        }
        final String name = session.getSessionName();
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

        /**
         * Creates a cache entry.
         *
         * @param session the session to cache.
         * @param disposable the disposable to evict the session from the cache.
         */
        private Entry(ReactorSession session, Disposable disposable) {
            super(false);
            this.session = session;
            this.disposable = disposable;
        }

        /**
         * Gets the session cached in the entry.
         *
         * @return the session.
         */
        private ReactorSession getSession() {
            return session;
        }

        /**
         * Await for the cached session to close.
         *
         * @return a Mono that completes when the session is closed.
         */
        private Mono<Void> awaitSessionClose() {
            return session.isClosed();
        }

        /**
         * Dispose of the cached session and the eviction disposable.
         */
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
