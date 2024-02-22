// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

import com.azure.core.amqp.exception.AmqpErrorContext;
import com.azure.core.amqp.implementation.handler.SessionHandler;
import com.azure.core.amqp.implementation.ProtonSession.ProtonChannel;
import org.apache.qpid.proton.amqp.transport.ErrorCondition;
import org.apache.qpid.proton.engine.EndpointState;
import org.apache.qpid.proton.engine.Receiver;
import org.apache.qpid.proton.engine.Sender;
import org.apache.qpid.proton.engine.Session;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

/**
 * Temporary type to support direct operations on Qpid Proton-j low-level {@link Session} in v1 and operations on
 * v2 ({@link ProtonSession}) side by side.
 * <p>
 * TODO (anu): remove the temporary type once v1's side by side support with v2 is no longer needed.
 * </p>
 */
public final class ProtonSessionWrapper {
    private final Session sessionUnsafe;
    private final ProtonSession session;
    private final String sessionName;
    private final String hostName;
    private final String connectionId;
    private final SessionHandler handler;
    private final ReactorProvider provider;

    public ProtonSessionWrapper(Session sessionUnsafe, String sessionName, String hostName, String connectionId,
        SessionHandler handler, ReactorProvider provider) {
        // V1
        this.sessionUnsafe = Objects.requireNonNull(sessionUnsafe, "'sessionUnsafe' cannot be null.");
        this.sessionName = Objects.requireNonNull(sessionName, "'sessionName' cannot be null.");
        this.hostName = Objects.requireNonNull(hostName, "'hostName' cannot be null.");
        this.connectionId = Objects.requireNonNull(connectionId, "'connectionId' cannot be null.");
        this.handler = Objects.requireNonNull(handler, "'handler' cannot be null.");
        this.provider = Objects.requireNonNull(provider, "'provider' cannot be null.");
        this.session = null;
    }

    ProtonSessionWrapper(ProtonSession session) {
        // V2
        this.session = Objects.requireNonNull(session, "'session' cannot be null.");
        this.sessionName = null;
        this.hostName = null;
        this.connectionId = null;
        this.handler = null;
        this.provider = null;
        this.sessionUnsafe = null;
    }

    String getName() {
        if (session != null) {
            return session.getName();
        } else {
            return sessionName;
        }
    }

    String getConnectionId() {
        if (session != null) {
            return session.getConnectionId();
        } else {
            return connectionId;
        }
    }

    String getFullyQualifiedNamespace() {
        if (session != null) {
            return session.getFullyQualifiedNamespace();
        } else {
            return hostName;
        }
    }

    Flux<EndpointState> getEndpointStates() {
        if (session != null) {
            return session.getEndpointStates();
        } else {
            return handler.getEndpointStates();
        }
    }

    ReactorProvider getReactorProvider() {
        if (session != null) {
            return session.getReactorProvider();
        } else {
            return provider;
        }
    }

    AmqpErrorContext getErrorContext() {
        if (session != null) {
            return session.getErrorContext();
        } else {
            return handler.getErrorContext();
        }
    }

    void openUnsafeIfV1() {
        if (sessionUnsafe != null) {
            sessionUnsafe.open();
        }
    }

    Mono<Void> open() {
        if (session != null) {
            return session.open();
        } else {
            return Mono.error(new UnsupportedOperationException("async open() requires v2 mode."));
        }
    }

    Mono<ProtonChannelWrapper> channel(String name, Duration timeout) {
        if (session != null) {
            // TODO (anu) - When removing v1 (hence the ProtonSessionWrapper), the ReactorSession::channel() API
            //  will directly invoke ProtonSession::channel().
            //
            return session.channel(name, timeout).map(ProtonChannelWrapper::new);
        } else {
            return Mono.just(new ProtonChannelWrapper(name, sessionUnsafe));
        }
    }

    Sender senderUnsafe(String name) {
        if (session != null) {
            return session.senderUnsafe(name);
        } else {
            return sessionUnsafe.sender(name);
        }
    }

    Receiver receiverUnsafe(String name) {
        if (session != null) {
            return session.receiverUnsafe(name);
        } else {
            return sessionUnsafe.receiver(name);
        }
    }

    void beginClose(ErrorCondition condition) {
        if (session != null) {
            session.beginClose(condition);
        } else {
            if (sessionUnsafe.getLocalState() != EndpointState.CLOSED) {
                sessionUnsafe.close();
                if (condition != null && sessionUnsafe.getCondition() == null) {
                    sessionUnsafe.setCondition(condition);
                }
            }
        }
    }

    void endClose() {
        if (session != null) {
            session.endClose();
        } else {
            handler.close();
        }
    }

    static final class ProtonChannelWrapper {
        private final boolean isV2;
        private final String name;
        private final Sender sender;
        private final Receiver receiver;

        ProtonChannelWrapper(String name, Session sessionUnsafe) {
            this.isV2 = false;
            this.name = Objects.requireNonNull(name, "'name' cannot be null.");
            Objects.requireNonNull(sessionUnsafe, "'sessionUnsafe' cannot be null.");
            // In v1 the RequestResponseChannel's sender and receiver gets created outside of dispatcher thread.
            // Continue to do the same here (i.e, no behavioral change) in wrapper when in v1 mode.
            this.sender = sessionUnsafe.sender(name + ":sender");
            this.receiver = sessionUnsafe.receiver(name + ":receiver");
        }

        ProtonChannelWrapper(ProtonChannel channel) {
            this.isV2 = true;
            Objects.requireNonNull(channel, "'channel' cannot be null.");
            this.name = channel.getName();
            this.sender = channel.getSender();
            this.receiver = channel.getReceiver();
        }

        boolean isV2() {
            return isV2;
        }

        String getName() {
            return name;
        }

        Sender sender() {
            return sender;
        }

        Receiver receiver() {
            return receiver;
        }
    }
}
