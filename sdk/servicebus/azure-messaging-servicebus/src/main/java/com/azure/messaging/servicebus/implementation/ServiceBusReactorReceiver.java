// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.messaging.servicebus.implementation;

import com.azure.core.amqp.AmqpConnection;
import com.azure.core.amqp.AmqpEndpointState;
import com.azure.core.amqp.AmqpRetryPolicy;
import com.azure.core.amqp.implementation.ReactorProvider;
import com.azure.core.amqp.implementation.ReactorReceiver;
import com.azure.core.amqp.implementation.TokenManager;
import com.azure.core.amqp.implementation.handler.ReceiveLinkHandler;
import com.azure.core.util.logging.ClientLogger;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.messaging.Source;
import org.apache.qpid.proton.amqp.transport.DeliveryState;
import org.apache.qpid.proton.amqp.transport.ErrorCondition;
import org.apache.qpid.proton.engine.Receiver;
import org.apache.qpid.proton.message.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.azure.core.amqp.implementation.ClientConstants.ENTITY_PATH_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.LINK_NAME_KEY;
import static com.azure.messaging.servicebus.implementation.ServiceBusReactorSession.LOCKED_UNTIL_UTC;
import static com.azure.messaging.servicebus.implementation.ServiceBusReactorSession.SESSION_FILTER;

/**
 * A proton-j receiver for Service Bus.
 */
public class ServiceBusReactorReceiver extends ReactorReceiver implements ServiceBusReceiveLink {
//    private static final Message EMPTY_MESSAGE = Proton.message();

    private final ClientLogger logger;
    private final AtomicBoolean isDisposed = new AtomicBoolean();
    private final ReceiveLinkHandler handler;
    private final Mono<String> sessionIdMono;
    private final Mono<OffsetDateTime> sessionLockedUntil;

    public ServiceBusReactorReceiver(AmqpConnection connection, String entityPath, Receiver receiver,
        ReceiveLinkHandler handler, TokenManager tokenManager, ReactorProvider provider, Duration timeout,
        AmqpRetryPolicy retryPolicy) {
        super(connection, entityPath, receiver, handler, tokenManager, provider.getReactorDispatcher(),
            retryPolicy.getRetryOptions());
        this.handler = handler;

        Map<String, Object> loggingContext = new HashMap<>(2);
        loggingContext.put(LINK_NAME_KEY, this.handler.getLinkName());
        loggingContext.put(ENTITY_PATH_KEY, entityPath);
        this.logger = new ClientLogger(ServiceBusReactorReceiver.class, loggingContext);

        this.sessionIdMono = getEndpointStates().filter(x -> x == AmqpEndpointState.ACTIVE)
            .next()
            .flatMap(state -> {
                @SuppressWarnings("unchecked") final Map<Symbol, Object> remoteSource =
                    ((Source) receiver.getRemoteSource()).getFilter();
                final Object value = remoteSource.get(SESSION_FILTER);
                if (value == null) {
                    logger.info("There is no session id.");
                    return Mono.empty();
                }

                final String actualSessionId = String.valueOf(value);
                return Mono.just(actualSessionId);
            })
            .cache(value -> Duration.ofMillis(Long.MAX_VALUE), error -> Duration.ZERO, () -> Duration.ZERO);

        this.sessionLockedUntil = getEndpointStates().filter(x -> x == AmqpEndpointState.ACTIVE)
            .next()
            .map(state -> {
                if (receiver.getRemoteProperties() != null
                    && receiver.getRemoteProperties().containsKey(LOCKED_UNTIL_UTC)) {
                    final long ticks = (long) receiver.getRemoteProperties().get(LOCKED_UNTIL_UTC);
                    return MessageUtils.convertDotNetTicksToOffsetDateTime(ticks);
                } else {
                    logger.info("Locked until not set.");

                    return Instant.EPOCH.atOffset(ZoneOffset.UTC);
                }
            })
            .cache(value -> Duration.ofMillis(Long.MAX_VALUE), error -> Duration.ZERO, () -> Duration.ZERO);
    }

    @Override
    public Mono<Void> updateDisposition(String lockToken, DeliveryState deliveryState) {
        return handler.sendDisposition(lockToken, deliveryState);
    }

    @Override
    public Flux<Message> receive() {
        return super.receive()
            .publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<String> getSessionId() {
        return sessionIdMono;
    }

    @Override
    public Mono<OffsetDateTime> getSessionLockedUntil() {
        return sessionLockedUntil;
    }

    @Override
    public Mono<Void> closeAsync() {
        return closeAsync("User invoked close operation.", null);
    }

    @Override
    protected Mono<Void> closeAsync(String message, ErrorCondition errorCondition) {
        return super.closeAsync(message, errorCondition);
    }
}
