// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation.handler;

import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.amqp.exception.AmqpErrorCondition;
import com.azure.core.amqp.exception.AmqpException;
import com.azure.core.amqp.implementation.ReactorDispatcher;
import com.azure.core.util.logging.ClientLogger;
import org.apache.qpid.proton.amqp.messaging.Accepted;
import org.apache.qpid.proton.amqp.messaging.Released;
import org.apache.qpid.proton.amqp.transaction.Declared;
import org.apache.qpid.proton.amqp.transport.DeliveryState;
import org.apache.qpid.proton.engine.Delivery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReceiverUnsettledDeliveriesTest {
    private static final String HOSTNAME = "hostname";
    private static final String ENTITY_PATH = "/orders";
    private static final String RECEIVER_LINK_NAME = "orders-link";
    final String DISPOSITION_ERROR_ON_CLOSE = "The receiver didn't receive the disposition acknowledgment "
        + "due to receive link closure.";
    private final ClientLogger logger = new ClientLogger(ReceiverUnsettledDeliveriesTest.class);
    private final AmqpRetryOptions retryOptions = new AmqpRetryOptions();
    private AutoCloseable mocksCloseable;
    @Mock
    private ReactorDispatcher reactorDispatcher;
    @Mock
    private Delivery delivery;

    @BeforeEach
    public void setup() throws IOException {
        mocksCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void teardown() throws Exception {
        Mockito.framework().clearInlineMock(this);

        if (mocksCloseable != null) {
            mocksCloseable.close();
        }
    }

    @Test
    public void tracksOnDelivery() throws IOException {
        doNothing().when(reactorDispatcher).invoke(any(Runnable.class));

        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            final UUID deliveryTag = UUID.randomUUID();
            deliveries.onDelivery(deliveryTag, delivery);
            assertTrue(deliveries.containsDelivery(deliveryTag));
        }
    }

    @Test
    public void sendDispositionErrorsForUntrackedDelivery() {
        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            final UUID deliveryTag = UUID.randomUUID();
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), Accepted.getInstance());
            StepVerifier.create(dispositionMono)
                .verifyError(IllegalArgumentException.class);
        }
    }

    @Test
    public void sendDispositionErrorsOnDispatcherIOException() throws IOException {
        doThrow(new IOException()).when(reactorDispatcher).invoke(any(Runnable.class));

        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            final UUID deliveryTag = UUID.randomUUID();
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), Accepted.getInstance());
            StepVerifier.create(dispositionMono)
                .verifyErrorSatisfies(error -> {
                    Assertions.assertTrue(error instanceof AmqpException);
                    Assertions.assertNotNull(error.getCause());
                    Assertions.assertTrue(error.getCause() instanceof IOException);
                });
        }
    }

    @Test
    public void sendDispositionErrorsOnDispatcherRejectedException() throws IOException {
        doThrow(new RejectedExecutionException()).when(reactorDispatcher).invoke(any(Runnable.class));

        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            final UUID deliveryTag = UUID.randomUUID();
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), Accepted.getInstance());
            StepVerifier.create(dispositionMono)
                .verifyErrorSatisfies(error -> {
                    Assertions.assertTrue(error instanceof AmqpException);
                    Assertions.assertNotNull(error.getCause());
                    Assertions.assertTrue(error.getCause() instanceof RejectedExecutionException);
                });
        }
    }

    @Test
    public void sendDispositionCompletesOnSuccessfulOutcome() throws IOException {
        final UUID deliveryTag = UUID.randomUUID();
        final DeliveryState desiredState = Accepted.getInstance();
        final DeliveryState remoteState = desiredState;

        doAnswer(byRunningRunnable()).when(reactorDispatcher).invoke(any(Runnable.class));
        when(delivery.getRemoteState()).thenReturn(remoteState);
        when(delivery.remotelySettled()).thenReturn(true);

        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), desiredState);
            dispositionMono.subscribe();
            deliveries.onDispositionAck(deliveryTag, delivery);
            StepVerifier.create(dispositionMono).verifyComplete();
            Assertions.assertFalse(deliveries.containsDelivery(deliveryTag));
        }
    }

    @Test
    public void sendDispositionErrorsOnReleaseOutcome() throws IOException {
        final UUID deliveryTag = UUID.randomUUID();
        final DeliveryState desiredState = Accepted.getInstance();
        final DeliveryState remoteState = Released.getInstance();

        doAnswer(byRunningRunnable()).when(reactorDispatcher).invoke(any(Runnable.class));
        when(delivery.getRemoteState()).thenReturn(remoteState);
        when(delivery.remotelySettled()).thenReturn(true);

        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), desiredState);
            dispositionMono.subscribe();
            deliveries.onDispositionAck(deliveryTag, delivery);
            StepVerifier.create(dispositionMono)
                .verifyErrorSatisfies(error -> {
                    Assertions.assertTrue(error instanceof AmqpException);
                    final AmqpException amqpError = (AmqpException) error;
                    Assertions.assertNotNull(amqpError.getErrorCondition());
                    Assertions.assertEquals(AmqpErrorCondition.OPERATION_CANCELLED, amqpError.getErrorCondition());
                });
            Assertions.assertFalse(deliveries.containsDelivery(deliveryTag));
        }
    }

    @Test
    public void sendDispositionErrorsOnUnknownOutcome() throws IOException {
        final UUID deliveryTag = UUID.randomUUID();
        final DeliveryState desiredState = Accepted.getInstance();
        final DeliveryState remoteState = new Declared();

        doAnswer(byRunningRunnable()).when(reactorDispatcher).invoke(any(Runnable.class));
        when(delivery.getRemoteState()).thenReturn(remoteState);
        when(delivery.remotelySettled()).thenReturn(true);

        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), desiredState);
            dispositionMono.subscribe();
            deliveries.onDispositionAck(deliveryTag, delivery);
            StepVerifier.create(dispositionMono)
                .verifyErrorSatisfies(error -> {
                    Assertions.assertTrue(error instanceof AmqpException);
                    Assertions.assertEquals(remoteState.toString(), error.getMessage());
                });
            Assertions.assertFalse(deliveries.containsDelivery(deliveryTag));
        }
    }

    @Test
    public void sendDispositionMonoReplaysCompletion() throws IOException {
        final UUID deliveryTag = UUID.randomUUID();
        final DeliveryState desiredState = Accepted.getInstance();
        final DeliveryState remoteState = desiredState;
        final AnswerCountAndRunRunnable answer = new AnswerCountAndRunRunnable();
        doAnswer(answer).when(reactorDispatcher).invoke(any(Runnable.class));
        when(delivery.getRemoteState()).thenReturn(remoteState);
        when(delivery.remotelySettled()).thenReturn(true);

        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), desiredState);
            dispositionMono.subscribe();
            deliveries.onDispositionAck(deliveryTag, delivery);
            for (int i = 0; i < 3; i++) {
                StepVerifier.create(dispositionMono).verifyComplete();
            }
            Assertions.assertEquals(1, answer.getInvocationCount());
        }
    }

    @Test
    public void sendDispositionMonoReplaysError() throws IOException {
        final UUID deliveryTag = UUID.randomUUID();
        final DeliveryState desiredState = Accepted.getInstance();
        final DeliveryState remoteState = new Declared();
        final AnswerCountAndRunRunnable answer = new AnswerCountAndRunRunnable();
        doAnswer(answer).when(reactorDispatcher).invoke(any(Runnable.class));
        when(delivery.getRemoteState()).thenReturn(remoteState);
        when(delivery.remotelySettled()).thenReturn(true);

        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), desiredState);
            dispositionMono.subscribe();
            deliveries.onDispositionAck(deliveryTag, delivery);

            final Throwable[] lastError = new Throwable[1];
            for (int i = 0; i < 3; i++) {
                StepVerifier.create(dispositionMono).verifyErrorSatisfies(error -> {
                    if (lastError[0] == null) {
                        lastError[0] = error;
                        return;
                    }
                    Assertions.assertEquals(lastError[0],
                        error,
                        "Expected replay of the last error object, but received a new error object.");
                });
            }
            Assertions.assertEquals(1, answer.getInvocationCount());
        }
    }

    @Test
    public void pendingSendDispositionErrorsOnClose() throws IOException {
        final UUID deliveryTag = UUID.randomUUID();
        final DeliveryState desiredState = Accepted.getInstance();
        final DeliveryState remoteState = new Declared();

        doAnswer(byRunningRunnable()).when(reactorDispatcher).invoke(any(Runnable.class));
        when(delivery.getRemoteState()).thenReturn(remoteState);
        when(delivery.remotelySettled()).thenReturn(true);

        final ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries();
        try {
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), desiredState);
            dispositionMono.subscribe();
            deliveries.close();
            StepVerifier.create(dispositionMono)
                .verifyErrorSatisfies(error -> {
                    Assertions.assertTrue(error instanceof AmqpException);
                    Assertions.assertEquals(DISPOSITION_ERROR_ON_CLOSE, error.getMessage());
                });
        } finally {
            deliveries.close();
        }
    }

    @Test
    public void preCloseAwaitForSendDispositionCompletion() throws IOException {
        final UUID deliveryTag = UUID.randomUUID();
        final DeliveryState desiredState = Accepted.getInstance();
        final DeliveryState remoteState = desiredState;

        doAnswer(byRunningRunnable()).when(reactorDispatcher).invoke(any(Runnable.class));
        when(delivery.getRemoteState()).thenReturn(remoteState);
        when(delivery.remotelySettled()).thenReturn(true);

        try (ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries()) {
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), desiredState);
            dispositionMono.subscribe();

            StepVerifier.create(deliveries.preClose())
                .then(() -> deliveries.onDispositionAck(deliveryTag, delivery))
                .verifyComplete();

            StepVerifier.create(dispositionMono).verifyComplete();
        }
    }

    @Test
    public void closeDoNotWaitForSendDispositionCompletion() throws IOException {
        final UUID deliveryTag = UUID.randomUUID();
        final DeliveryState desiredState = Accepted.getInstance();
        final DeliveryState remoteState = desiredState;

        doAnswer(byRunningRunnable()).when(reactorDispatcher).invoke(any(Runnable.class));
        when(delivery.getRemoteState()).thenReturn(remoteState);
        when(delivery.remotelySettled()).thenReturn(true);

        final ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries();
        try {
            deliveries.onDelivery(deliveryTag, delivery);
            final Mono<Void> dispositionMono = deliveries.sendDisposition(deliveryTag.toString(), desiredState);
            dispositionMono.subscribe();

            StepVerifier.create(Mono.<Void>fromRunnable(() -> deliveries.close()))
                .then(() -> deliveries.onDispositionAck(deliveryTag, delivery))
                .verifyComplete();

            StepVerifier.create(dispositionMono)
                .verifyErrorSatisfies(error -> {
                    Assertions.assertTrue(error instanceof AmqpException);
                    Assertions.assertEquals(DISPOSITION_ERROR_ON_CLOSE, error.getMessage());
                });
        } finally {
            deliveries.close();
        }
    }

    @Test
    public void settlesUnsettledDeliveriesOnClose() throws IOException {
        doAnswer(byRunningRunnable()).when(reactorDispatcher).invoke(any(Runnable.class));
        final ReceiverUnsettledDeliveries deliveries = createTestUnsettledDeliveries();
        try {
            deliveries.onDelivery(UUID.randomUUID(), delivery);
            deliveries.close();
            verify(delivery).disposition(any());
            verify(delivery).settle();
        } finally {
            deliveries.close();
        }
    }

    private ReceiverUnsettledDeliveries createTestUnsettledDeliveries() {
        return new ReceiverUnsettledDeliveries(HOSTNAME, ENTITY_PATH, RECEIVER_LINK_NAME,
            reactorDispatcher, retryOptions, logger);
    }

    private static Answer<Void> byRunningRunnable() {
        return invocation -> {
            final Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        };
    }

    private static final class AnswerCountAndRunRunnable implements Answer<Void> {
        private int invocationCount = 0;

        int getInvocationCount() {
            return invocationCount;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            invocationCount++;
            final Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }
    }
}
