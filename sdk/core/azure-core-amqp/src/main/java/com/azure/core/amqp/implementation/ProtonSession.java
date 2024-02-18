// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.amqp.exception.AmqpErrorCondition;
import com.azure.core.amqp.exception.AmqpErrorContext;
import com.azure.core.amqp.exception.AmqpException;
import com.azure.core.amqp.implementation.handler.SessionHandler;
import com.azure.core.util.logging.ClientLogger;
import org.apache.qpid.proton.amqp.transport.ErrorCondition;
import org.apache.qpid.proton.engine.BaseHandler;
import org.apache.qpid.proton.engine.Connection;
import org.apache.qpid.proton.engine.EndpointState;
import org.apache.qpid.proton.engine.Receiver;
import org.apache.qpid.proton.engine.Sender;
import org.apache.qpid.proton.engine.Session;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.azure.core.amqp.exception.AmqpErrorCondition.TIMEOUT_ERROR;
import static com.azure.core.amqp.implementation.ClientConstants.SESSION_NAME_KEY;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

/**
 * A type representing QPid Proton-j session {@link Session} endpoint.
 */
final class ProtonSession {
    private static final String SESSION_NOT_OPENED_MESSAGE = "session has not been opened.";
    private static final String REACTOR_SHUTDOWN_MESSAGE = "connection-reactor is disposed.";
    private static final String DISPOSED_MESSAGE_FORMAT = "Cannot create %s in a closed session.";
    private final AtomicReference<State> state = new AtomicReference<>(State.EMPTY);
    private final AtomicBoolean opened = new AtomicBoolean(false);
    private final Sinks.Empty<Void> openAwaiter = Sinks.empty();
    private final Connection connection;
    private final ReactorProvider reactorProvider;
    private final SessionHandler handler;
    private final ClientLogger logger;

    ProtonSession(String fullyQualifiedNamespace, String connectionId, Connection connection,
        ReactorHandlerProvider handlerProvider, ReactorProvider reactorProvider, String sessionName,
        Duration openTimeout, ClientLogger logger) {
        this.connection = Objects.requireNonNull(connection, "'connection' cannot be null.");
        this.reactorProvider = Objects.requireNonNull(reactorProvider, "'reactorProvider' cannot be null.");
        Objects.requireNonNull(handlerProvider, "'handlerProvider' cannot be null.");
        this.handler = handlerProvider.createSessionHandler(connectionId, fullyQualifiedNamespace, sessionName, openTimeout);
        this.logger = Objects.requireNonNull(logger, "'logger' cannot be null.");
    }

    /**
     * Gets the name of the session.
     *
     * @return the session name.
     */
    String getName() {
        return handler.getSessionName();
    }

    /**
     * Gets the hostname of the QPid Proton-j Connection facilitating the session.
     *
     * @return the hostname.
     */
    String getHostname() {
        return handler.getHostname();
    }

    /**
     * Gets the identifier of the QPid Proton-j Connection facilitating the session.
     *
     * @return the connection identifier.
     */
    String getConnectionId() {
        return handler.getConnectionId();
    }

    /**
     * Gets the error context of the session.
     *
     * @return the error context.
     */
    AmqpErrorContext getErrorContext() {
        return handler.getErrorContext();
    }

    /**
     * Gets the connectivity states of the session.
     *
     * @return the session's connectivity states.
     */
    Flux<EndpointState> getEndpointStates() {
        return handler.getEndpointStates();
    }

    /**
     * Gets the reactor dispatcher provider associated with the session.
     * <p>
     * Any operation (e.g., obtaining sender, receiver) on the session must be invoked on the reactor dispatcher.
     * </p>
     *
     * @return the reactor dispatcher provider.
     */
    ReactorProvider getReactorProvider() {
        return reactorProvider;
    }

    /**
     * Opens the session in the QPid Proton-j Connection.
     *
     * @return a mono that completes when the session is opened.
     */
    Mono<Void> open() {
        if (opened.getAndSet(true)) {
            return openAwaiter.asMono();
        }
        try {
            reactorProvider.getReactorDispatcher().invoke(() -> {
                final Session session = connection.session();
                BaseHandler.setHandler(session, handler);
                session.open();
                logger.atInfo().addKeyValue(SESSION_NAME_KEY, handler.getSessionName()).log("session local open scheduled.");

                final State s = state.compareAndExchange(State.EMPTY, new State(session));
                if (s == State.EMPTY) {
                    openAwaiter.emitEmpty(FAIL_FAST);
                } else {
                    session.close();
                    if (s == State.DISPOSED) {
                        openAwaiter.emitError(
                            retriableAmqpError(null, "session is disposed.", null), FAIL_FAST);
                    } else {
                        openAwaiter.emitError(new IllegalStateException("session is already opened."), FAIL_FAST);
                    }
                }
            });
        } catch (Exception e) {
            if (e instanceof IOException | e instanceof RejectedExecutionException) {
                openAwaiter.emitError(
                    retriableAmqpError(null, REACTOR_SHUTDOWN_MESSAGE, e), FAIL_FAST);
            } else {
                openAwaiter.emitError(e, FAIL_FAST);
            }
        }
        return openAwaiter.asMono();
    }

    /**
     * Gets a channel on the session for sending and receiving messages.
     *
     * @param name the channel name.
     * @param retryOptions the retry options.
     * @return a mono that completes with a {@link ProtonChannel} when the channel is created in the session.
     */
    Mono<ProtonChannel> channel(final String name, AmqpRetryOptions retryOptions) {
        final Mono<ProtonChannel> channel = Mono.create(sink -> {
            if (name == null) {
                sink.error(new NullPointerException("'name' cannot be null."));
                return;
            }
            try {
                getSession("channel");
            } catch (RuntimeException e) {
                sink.error(e);
                return;
            }
            try {
                reactorProvider.getReactorDispatcher().invoke(() -> {
                    final Session session;
                    try {
                        session = getSession("channel");
                    } catch (RuntimeException e) {
                        sink.error(e);
                        return;
                    }
                    final String senderName = name + ":sender";
                    final String receiverName = name + ":receiver";
                    sink.success(new ProtonChannel(name, session.sender(senderName), session.receiver(receiverName)));
                });
            } catch (Exception e) {
                if (e instanceof IOException | e instanceof RejectedExecutionException) {
                    sink.error(retriableAmqpError(null, REACTOR_SHUTDOWN_MESSAGE, e));
                } else {
                    sink.error(e);
                }
            }
        });
        final Duration timeout = retryOptions.getTryTimeout().plusSeconds(2);
        return channel.timeout(timeout, Mono.error(() -> {
            final String message = "Obtaining channel timed out. name:" + name;
            return retriableAmqpError(TIMEOUT_ERROR, message, null);
        }));
    }

    /**
     * Gets a QPid Proton-j sender on the session.
     * <p>
     * The call site required to invoke this method on Reactor dispatcher thread associated with the session.
     * It is possible to run into race conditions with proton-j if invoked from any other threads.
     * </p>
     * @see #getReactorProvider()
     *
     * @param name the sender name.
     * @return the sender.
     */
    Sender senderUnsafe(String name) {
        final Session session = getSession("sender-link");
        return session.sender(name);
    }

    /**
     * Gets a QPid Proton-j receiver on the session.
     * <p>
     * The call site required to invoke this method on Reactor dispatcher thread associated with the session.
     * It is possible to run into race conditions with proton-j if invoked from any other threads.
     * </p>
     * @see #getReactorProvider()
     *
     * @param name the sender name.
     * @return the sender.
     */
    Receiver receiverUnsafe(String name) {
        final Session session = getSession("receive-link");
        return session.receiver(name);
    }

    void beginClose(ErrorCondition errorCondition) {
        final State s = state.getAndSet(State.DISPOSED);
        if (s == State.EMPTY || s == State.DISPOSED) {
            return;
        }
        final Session session = s.get();
        if (session.getLocalState() != EndpointState.CLOSED) {
            session.close();
            if (errorCondition != null && session.getCondition() == null) {
                session.setCondition(errorCondition);
            }
        }
    }

    void endClose() {
        handler.close();
    }

    private Session getSession(String resourceName) {
        final State s = state.get();
        if (s == State.EMPTY) {
            throw new IllegalStateException(SESSION_NOT_OPENED_MESSAGE);
        }
        if (s == State.DISPOSED) {
            throw retriableAmqpError(null, String.format(DISPOSED_MESSAGE_FORMAT, resourceName), null);
        }
        return s.get();
    }

    private AmqpException retriableAmqpError(AmqpErrorCondition condition, String message, Throwable cause) {
        // The call sites uses this method to translate a session unavailability "event" (session disposed, or
        // connection being closed) to a retriable error.
        // While the "event" is transient, resolving it (e.g. by creating a new session on current connection or
        // on a new connection) needs to be done not by the parent 'AmqpConnection' owning this ProtonSession but
        // by the downstream layer. E.g., the downstream Consumer, Producer Client that has access to the chain to
        // propagate retry request to top level connection-cache (or v1 connection-processor).
        return new AmqpException(true, condition, message, cause, handler.getErrorContext());
    }

    static final class ProtonChannel {
        private final String name;
        private final Sender sender;
        private final Receiver receiver;

        ProtonChannel(String name, Sender sender, Receiver receiver) {
            this.name = name;
            this.sender = sender;
            this.receiver = receiver;
        }

        String getName() {
            return name;
        }

        Sender getSender() {
            return sender;
        }

        Receiver getReceiver() {
            return receiver;
        }
    }

    private static final class State {
        private static final State EMPTY = new State();
        private static final State DISPOSED = new State();
        private final Session session;

        private State() {
            // Ctr for the EMPTY and DISPOSED state.
            this.session = null;
        }

        private State(Session session) {
            this.session = Objects.requireNonNull(session, "'session' cannot be null.");
        }

        private Session get() {
            assert this != EMPTY;
            return this.session;
        }
    }
}
