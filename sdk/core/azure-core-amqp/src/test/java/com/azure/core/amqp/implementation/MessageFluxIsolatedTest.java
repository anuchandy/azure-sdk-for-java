// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

import com.azure.core.amqp.AmqpEndpointState;
import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.amqp.AmqpRetryPolicy;
import com.azure.core.amqp.FixedAmqpRetryPolicy;
import com.azure.core.amqp.exception.AmqpException;
import org.apache.qpid.proton.message.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.AtLeast;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Execution(ExecutionMode.SAME_THREAD)
@Isolated
public class MessageFluxIsolatedTest {
    private static final int MAX_RETRY = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(3);
    private static final Duration UPSTREAM_DELAY_BEFORE_NEXT = RETRY_DELAY.plusSeconds(1);
    private final AmqpRetryOptions retryOptions = new AmqpRetryOptions().setMaxRetries(MAX_RETRY).setDelay(RETRY_DELAY);
    private final  AmqpRetryPolicy retryPolicy = new FixedAmqpRetryPolicy(retryOptions);

    private AutoCloseable mocksCloseable;

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
    @Execution(ExecutionMode.SAME_THREAD)
    public void shouldGetNextReceiverWhenCurrentTerminateWithRetriableError() {
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver firstReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade firstReceiverFacade = new ReactorReceiverFacade(upstream, firstReceiver);
        when(firstReceiver.getEndpointStates()).thenReturn(firstReceiverFacade.getEndpointStates());
        when(firstReceiver.receive()).thenReturn(firstReceiverFacade.getMessages());
        when(firstReceiver.closeAsync()).thenReturn(Mono.empty());

        final ReactorReceiver secondReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade secondReceiverFacade = new ReactorReceiverFacade(upstream, secondReceiver);
        when(secondReceiver.getEndpointStates()).thenReturn(secondReceiverFacade.getEndpointStates());
        when(secondReceiver.receive()).thenReturn(secondReceiverFacade.getMessages());
        when(secondReceiver.closeAsync()).thenReturn(Mono.empty());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(firstReceiverFacade.emit())
                .then(firstReceiverFacade.errorEndpointStates(new AmqpException(true, "retriable", null)))
                .then(firstReceiverFacade.completeMessages())
                .thenAwait(UPSTREAM_DELAY_BEFORE_NEXT)
                .then(secondReceiverFacade.emit())
                .then(() -> upstream.complete())
                .verifyComplete();
        }

        Assertions.assertTrue(firstReceiverFacade.wasSubscribedToMessages());
        Assertions.assertTrue(secondReceiverFacade.wasSubscribedToMessages());
        verify(firstReceiver).closeAsync();
        verify(secondReceiver).closeAsync();
        upstream.assertCancelled();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void shouldGetNextReceiverWhenCurrentTerminateWithCompletion() {
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver firstReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade firstReceiverFacade = new ReactorReceiverFacade(upstream, firstReceiver);
        when(firstReceiver.getEndpointStates()).thenReturn(firstReceiverFacade.getEndpointStates());
        when(firstReceiver.receive()).thenReturn(firstReceiverFacade.getMessages());
        when(firstReceiver.closeAsync()).thenReturn(Mono.empty());

        final ReactorReceiver secondReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade secondReceiverFacade = new ReactorReceiverFacade(upstream, secondReceiver);
        when(secondReceiver.getEndpointStates()).thenReturn(secondReceiverFacade.getEndpointStates());
        when(secondReceiver.receive()).thenReturn(secondReceiverFacade.getMessages());
        when(secondReceiver.closeAsync()).thenReturn(Mono.empty());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(firstReceiverFacade.emit())
                .then(firstReceiverFacade.completeEndpointStates())
                .then(firstReceiverFacade.completeMessages())
                .thenAwait(UPSTREAM_DELAY_BEFORE_NEXT)
                .then(secondReceiverFacade.emit())
                .then(() -> upstream.complete())
                .verifyComplete();
        }

        Assertions.assertTrue(firstReceiverFacade.wasSubscribedToMessages());
        Assertions.assertTrue(secondReceiverFacade.wasSubscribedToMessages());
        verify(firstReceiver).closeAsync();
        verify(secondReceiver).closeAsync();
        upstream.assertCancelled();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void shouldNotGetNextReceiverWhenCurrentTerminateWithNonRetriableError() {
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver firstReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade firstReceiverFacade = new ReactorReceiverFacade(upstream, firstReceiver);
        when(firstReceiver.getEndpointStates()).thenReturn(firstReceiverFacade.getEndpointStates());
        when(firstReceiver.receive()).thenReturn(firstReceiverFacade.getMessages());
        when(firstReceiver.closeAsync()).thenReturn(Mono.empty());

        final ReactorReceiver secondReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade secondReceiverFacade = new ReactorReceiverFacade(upstream, secondReceiver);
        when(secondReceiver.getEndpointStates()).thenReturn(secondReceiverFacade.getEndpointStates());
        when(secondReceiver.receive()).thenReturn(secondReceiverFacade.getMessages());
        when(secondReceiver.closeAsync()).thenReturn(Mono.empty());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(firstReceiverFacade.emit())
                .then(firstReceiverFacade.errorEndpointStates(new AmqpException(false, "non-retriable", null)))
                .then(firstReceiverFacade.completeMessages())
                .thenAwait(UPSTREAM_DELAY_BEFORE_NEXT)
                .then(secondReceiverFacade.emit())
                .then(() -> upstream.complete())
                .verifyError();
        }

        Assertions.assertTrue(firstReceiverFacade.wasSubscribedToMessages());
        Assertions.assertFalse(secondReceiverFacade.wasSubscribedToMessages());
        // Expecting closeAsync invocation from two call sites -
        // 1. after identified that receiver is terminated.
        // 2. when operator terminates due to non-retriable error.
        verify(firstReceiver, new AtLeast(2)).closeAsync();
        verify(secondReceiver, never()).closeAsync();
        upstream.assertCancelled();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void shouldTerminateWhenRetriesOfReceiversErrorExhausts() {
        final AmqpException error = new AmqpException(true, "retriable", null);
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver receiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade receiverFacade = new ReactorReceiverFacade(upstream,
            receiver,
            TestPublisher.<Message>createCold());
        when(receiver.getEndpointStates()).thenReturn(receiverFacade.getEndpointStates());
        when(receiver.receive()).thenReturn(receiverFacade.getMessages());
        when(receiver.closeAsync()).thenReturn(Mono.empty());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(receiverFacade.emit())
                .then(receiverFacade.errorEndpointStates(error))
                .then(receiverFacade.completeMessages())
                .thenAwait(UPSTREAM_DELAY_BEFORE_NEXT)
                .then(receiverFacade.emit())                // response for retry1
                .then(receiverFacade.errorEndpointStates(error))
                .then(receiverFacade.completeMessages())
                .thenAwait(UPSTREAM_DELAY_BEFORE_NEXT)
                .then(receiverFacade.emit())                // response for retry2
                .then(receiverFacade.errorEndpointStates(error))
                .then(receiverFacade.completeMessages())
                .thenAwait(UPSTREAM_DELAY_BEFORE_NEXT)
                .then(receiverFacade.emit())                // response for retry3
                .then(receiverFacade.errorEndpointStates(error))
                .then(receiverFacade.completeMessages())
                .verifyErrorMatches(e -> e == error);
        }

        Assertions.assertEquals(MAX_RETRY + 1, receiverFacade.getSubscriptionCountToMessages());
        upstream.assertCancelled();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void receiverShouldGetRequestOnceEndpointIsActive() {
        final int request = 5;
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver receiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade receiverFacade = new ReactorReceiverFacade(upstream, receiver);
        when(receiver.getEndpointStates()).thenReturn(receiverFacade.getEndpointStates());
        when(receiver.receive()).thenReturn(receiverFacade.getMessages());
        when(receiver.closeAsync()).thenReturn(Mono.empty());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(receiverFacade.emit())
                .thenRequest(request)
                .then(receiverFacade.emitAndCompleteEndpointStates(AmqpEndpointState.ACTIVE))
                .then(receiverFacade.completeMessages())
                .then(() -> upstream.complete())
                .verifyComplete();
        }

        Assertions.assertEquals(request, receiverFacade.getRequestedMessages());
        upstream.assertCancelled();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void receiverShouldNotGetRequestIfEndpointIsNeverActive() {
        final int request = 5;
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver receiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade receiverFacade = new ReactorReceiverFacade(upstream, receiver);
        when(receiver.getEndpointStates()).thenReturn(receiverFacade.getEndpointStates());
        when(receiver.receive()).thenReturn(receiverFacade.getMessages());
        when(receiver.closeAsync()).thenReturn(Mono.empty());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(receiverFacade.emit())
                .thenRequest(request)
                .then(receiverFacade.emitAndCompleteEndpointStates(AmqpEndpointState.UNINITIALIZED))
                .then(receiverFacade.completeMessages())
                .then(() -> upstream.complete())
                .verifyComplete();
        }

        Assertions.assertEquals(0, receiverFacade.getRequestedMessages());
        upstream.assertCancelled();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void shouldTransferRequestToNextReceiver() {
        final int request = 10;
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver firstReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade firstReceiverFacade = new ReactorReceiverFacade(upstream, firstReceiver);
        final AmqpException error = new AmqpException(true, "retriable", null);
        when(firstReceiver.getEndpointStates()).thenReturn(firstReceiverFacade.getEndpointStates());
        when(firstReceiver.receive()).thenReturn(firstReceiverFacade.getMessages());
        when(firstReceiver.closeAsync()).thenReturn(Mono.empty());

        final ReactorReceiver secondReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade secondReceiverFacade = new ReactorReceiverFacade(upstream, secondReceiver);
        when(secondReceiver.getEndpointStates()).thenReturn(secondReceiverFacade.getEndpointStates());
        when(secondReceiver.receive()).thenReturn(secondReceiverFacade.getMessages());
        when(secondReceiver.closeAsync()).thenReturn(Mono.empty());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(firstReceiverFacade.emit())
                .thenRequest(request)
                .then(firstReceiverFacade.emitAndErrorEndpointStates(AmqpEndpointState.ACTIVE, error))
                .then(firstReceiverFacade.completeMessages())
                .thenAwait(UPSTREAM_DELAY_BEFORE_NEXT)
                .then(secondReceiverFacade.emit())
                .then(secondReceiverFacade.emitAndCompleteEndpointStates(AmqpEndpointState.ACTIVE))
                .then(secondReceiverFacade.completeMessages())
                .then(() -> upstream.complete())
                .verifyComplete();
        }

        Assertions.assertEquals(request, firstReceiverFacade.getRequestedMessages());
        Assertions.assertEquals(request, secondReceiverFacade.getRequestedMessages());
        upstream.assertCancelled();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void shouldTransferPendingRequestToNextReceiver() {
        final Duration retryDelay = Duration.ofSeconds(1);
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver firstReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade firstReceiverFacade = new ReactorReceiverFacade(upstream, firstReceiver);
        when(firstReceiver.getEndpointStates()).thenReturn(firstReceiverFacade.getEndpointStates());
        when(firstReceiver.receive()).thenReturn(firstReceiverFacade.getMessages());
        when(firstReceiver.closeAsync()).thenReturn(Mono.empty());

        final ReactorReceiver secondReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade secondReceiverFacade = new ReactorReceiverFacade(upstream, secondReceiver);
        when(secondReceiver.getEndpointStates()).thenReturn(secondReceiverFacade.getEndpointStates());
        when(secondReceiver.receive()).thenReturn(secondReceiverFacade.getMessages());
        when(secondReceiver.closeAsync()).thenReturn(Mono.empty());

        final Message message = mock(Message.class);
        final List<Message> firstReceiverMessages = generateMessages(message, 4);
        final List<Message> secondReceiverMessages = generateMessages(message, 6);
        final int firstReceiverMessagesCount = firstReceiverMessages.size();
        final int secondReceiverMessagesCount = secondReceiverMessages.size();
        final int request = firstReceiverMessagesCount + secondReceiverMessagesCount + 5;

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(firstReceiverFacade.emit())
                .thenRequest(request)
                .then(firstReceiverFacade.emitAndCompleteEndpointStates(AmqpEndpointState.ACTIVE))
                .then(firstReceiverFacade.emitAndCompleteMessages(firstReceiverMessages))
                .thenAwait(retryDelay.plusSeconds(1))
                .then(secondReceiverFacade.emit())
                .then(secondReceiverFacade.emitAndCompleteEndpointStates(AmqpEndpointState.ACTIVE))
                .then(secondReceiverFacade.emitAndCompleteMessages(secondReceiverMessages))
                .then(() -> upstream.complete())
                .expectNextCount(firstReceiverMessagesCount + secondReceiverMessagesCount)
                .thenConsumeWhile(m -> true)
                .verifyComplete();
        }

        Assertions.assertEquals(request, firstReceiverFacade.getRequestedMessages());
        Assertions.assertEquals(request - firstReceiverMessagesCount, secondReceiverFacade.getRequestedMessages());
        upstream.assertCancelled();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void shouldDrainErroredReceiverBeforeGettingNextReceiver() {
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.createCold();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver firstReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade firstReceiverFacade = new ReactorReceiverFacade(upstream,
            firstReceiver,
            TestPublisher.<Message>createCold());
        final AmqpException error = new AmqpException(true, "retriable", null);
        when(firstReceiver.getEndpointStates()).thenReturn(firstReceiverFacade.getEndpointStates());
        when(firstReceiver.receive()).thenReturn(firstReceiverFacade.getMessages());
        when(firstReceiver.closeAsync()).thenReturn(Mono.empty());

        final ReactorReceiver secondReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade secondReceiverFacade = new ReactorReceiverFacade(upstream,
            secondReceiver,
            TestPublisher.<Message>createCold());
        when(secondReceiver.getEndpointStates()).thenReturn(secondReceiverFacade.getEndpointStates());
        when(secondReceiver.receive()).thenReturn(secondReceiverFacade.getMessages());
        when(secondReceiver.closeAsync()).thenReturn(Mono.empty());

        final Message message = mock(Message.class);
        final List<Message> firstReceiverMessages = generateMessages(message, 4);
        final List<Message> secondReceiverMessages = generateMessages(message, 6);
        final int firstReceiverMessagesCount = firstReceiverMessages.size();
        final int secondReceiverMessagesCount = secondReceiverMessages.size();
        final int request = firstReceiverMessagesCount + secondReceiverMessagesCount + 5;

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(firstReceiverFacade.emitAndErrorEndpointStates(AmqpEndpointState.ACTIVE, error))
                .then(firstReceiverFacade.emitAndCompleteMessages(firstReceiverMessages))
                .then(secondReceiverFacade.emitAndCompleteEndpointStates(AmqpEndpointState.ACTIVE))
                .then(secondReceiverFacade.emitAndCompleteMessages(secondReceiverMessages))
                .thenRequest(request)
                .then(firstReceiverFacade.emit())
                .thenAwait(UPSTREAM_DELAY_BEFORE_NEXT)
                .then(secondReceiverFacade.emit())
                .then(() -> upstream.complete())
                .thenConsumeWhile(m -> true)
                .verifyComplete();
        }

        Assertions.assertEquals(firstReceiverMessagesCount, firstReceiverFacade.getMessageEmitCount());
        Assertions.assertEquals(secondReceiverMessagesCount, secondReceiverFacade.getMessageEmitCount());
        upstream.assertCancelled();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void shouldDrainCompletedReceiverBeforeGettingNextReceiver() {
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.createCold();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), 0, retryPolicy);

        final ReactorReceiver firstReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade firstReceiverFacade = new ReactorReceiverFacade(upstream,
            firstReceiver,
            TestPublisher.<Message>createCold());
        when(firstReceiver.getEndpointStates()).thenReturn(firstReceiverFacade.getEndpointStates());
        when(firstReceiver.receive()).thenReturn(firstReceiverFacade.getMessages());
        when(firstReceiver.closeAsync()).thenReturn(Mono.empty());

        final ReactorReceiver secondReceiver = mock(ReactorReceiver.class);
        final ReactorReceiverFacade secondReceiverFacade = new ReactorReceiverFacade(upstream,
            secondReceiver,
            TestPublisher.<Message>createCold());
        when(secondReceiver.getEndpointStates()).thenReturn(secondReceiverFacade.getEndpointStates());
        when(secondReceiver.receive()).thenReturn(secondReceiverFacade.getMessages());
        when(secondReceiver.closeAsync()).thenReturn(Mono.empty());

        final Message message = mock(Message.class);
        final List<Message> firstReceiverMessages = generateMessages(message, 4);
        final List<Message> secondReceiverMessages = generateMessages(message, 6);
        final int firstReceiverMessagesCount = firstReceiverMessages.size();
        final int secondReceiverMessagesCount = secondReceiverMessages.size();
        final int request = firstReceiverMessagesCount + secondReceiverMessagesCount + 5;

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(firstReceiverFacade.emitAndCompleteEndpointStates(AmqpEndpointState.ACTIVE))
                .then(firstReceiverFacade.emitAndCompleteMessages(firstReceiverMessages))
                .then(secondReceiverFacade.emitAndCompleteEndpointStates(AmqpEndpointState.ACTIVE))
                .then(secondReceiverFacade.emitAndCompleteMessages(secondReceiverMessages))
                .thenRequest(request)
                .then(firstReceiverFacade.emit())
                .thenAwait(UPSTREAM_DELAY_BEFORE_NEXT)
                .then(secondReceiverFacade.emit())
                .then(() -> upstream.complete())
                .thenConsumeWhile(m -> true)
                .verifyComplete();
        }

        Assertions.assertEquals(firstReceiverMessagesCount, firstReceiverFacade.getMessageEmitCount());
        Assertions.assertEquals(secondReceiverMessagesCount, secondReceiverFacade.getMessageEmitCount());
        upstream.assertCancelled();
    }

    @Test
    public void initialCreditShouldBeSumOfDemandAndPrefetch() {
        final int prefetch = 100;
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), prefetch, retryPolicy);

        final ReactorReceiver receiver = mock(ReactorReceiver.class);
        when(receiver.receive()).thenReturn(Flux.never());
        when(receiver.getEndpointStates()).thenReturn(Flux.just(AmqpEndpointState.ACTIVE));
        when(receiver.closeAsync()).thenReturn(Mono.empty());

        final AtomicLong initialFlow = new AtomicLong();
        doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            @SuppressWarnings("unchecked")
            final Supplier<Long> creditSupplier = (Supplier<Long>) args[0];
            Assertions.assertNotNull(creditSupplier);
            initialFlow.addAndGet(creditSupplier.get());
            return null;
        }).when(receiver).scheduleFlow(any());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .thenRequest(10)
                .then(() -> upstream.next(receiver))
                .then(() -> upstream.complete())
                .verifyComplete();
        }

        Assertions.assertEquals(prefetch + 10, initialFlow.get());
        verify(receiver).closeAsync();
        upstream.assertCancelled();
    }

    @Test
    public void shouldSendAccumulatedCreditWhenDemandAccumulatedEqualsPrefetch() {
        final int prefetch = 100;
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), prefetch, retryPolicy);

        final ReactorReceiver receiver = mock(ReactorReceiver.class);
        when(receiver.receive()).thenReturn(Flux.never());
        when(receiver.getEndpointStates()).thenReturn(Flux.just(AmqpEndpointState.ACTIVE));
        when(receiver.closeAsync()).thenReturn(Mono.empty());

        final AtomicInteger flowCalls = new AtomicInteger();
        final AtomicLong firstFlow = new AtomicLong();
        final AtomicLong secondFlow = new AtomicLong();
        doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            @SuppressWarnings("unchecked")
            final Supplier<Long> creditSupplier = (Supplier<Long>) args[0];
            Assertions.assertNotNull(creditSupplier);

            final int calls = flowCalls.incrementAndGet();
            if (calls == 1) {
                firstFlow.set(creditSupplier.get());
            } else if (calls == 2) {
                secondFlow.set(creditSupplier.get());
            }
            return null;
        }).when(receiver).scheduleFlow(any());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(() -> upstream.next(receiver))
                .thenRequest(10)    // 10  -  0  + 100  = 110 [accumulatedCredit_110 >= 100]
                .thenAwait()
                .thenRequest(20)    // 30  - 110 + 100  = 20  [accumulatedCredit_20  <  100]
                .thenRequest(20)    // 50  - 130 + 100  = 20  [accumulatedCredit_40  <  100]
                .thenRequest(20)    // 70  - 150 + 100  = 20  [accumulatedCredit_60  <  100]
                .thenRequest(20)    // 90  - 170 + 100  = 20  [accumulatedCredit_80  <  100]
                .thenRequest(20)    // 110 - 190 + 100 =  20  [accumulatedCredit_100 >= 100]
                .then(() -> upstream.complete())
                .verifyComplete();
        }

        Assertions.assertEquals(2, flowCalls.get());
        Assertions.assertEquals(prefetch + 10, firstFlow.get());
        Assertions.assertEquals(prefetch, secondFlow.get());
        verify(receiver).closeAsync();
        upstream.assertCancelled();
    }

    @Test
    public void shouldSendAccumulatedCreditWhenDemandAccumulatedGreaterThanPrefetch() {
        final int prefetch = 100;
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), prefetch, retryPolicy);

        final ReactorReceiver receiver = mock(ReactorReceiver.class);
        when(receiver.receive()).thenReturn(Flux.never());
        when(receiver.getEndpointStates()).thenReturn(Flux.just(AmqpEndpointState.ACTIVE));
        when(receiver.closeAsync()).thenReturn(Mono.empty());

        final AtomicInteger flowCalls = new AtomicInteger();
        final AtomicLong firstFlow = new AtomicLong();
        final AtomicLong secondFlow = new AtomicLong();
        doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            @SuppressWarnings("unchecked")
            final Supplier<Long> creditSupplier = (Supplier<Long>) args[0];
            Assertions.assertNotNull(creditSupplier);

            final int calls = flowCalls.incrementAndGet();
            if (calls == 1) {
                firstFlow.set(creditSupplier.get());
            } else if (calls == 2) {
                secondFlow.set(creditSupplier.get());
            }
            return null;
        }).when(receiver).scheduleFlow(any());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(() -> upstream.next(receiver))
                .thenRequest(10)    // 10 -  0  + 100  = 110 [accumulatedCredit_110 >= 100]
                .thenAwait()
                .thenRequest(20)    // 30  - 110 + 100 =  20 [accumulatedCredit_20  <  100]
                .thenRequest(20)    // 50  - 130 + 100 =  20 [accumulatedCredit_40  <  100]
                .thenRequest(20)    // 70  - 150 + 100 =  20 [accumulatedCredit_60  <  100]
                .thenRequest(20)    // 90  - 170 + 100 =  20 [accumulatedCredit_80  <  100]
                .thenRequest(30)    // 120 - 190 + 100 =  30 [accumulatedCredit_110 >= 100]
                .then(() -> upstream.complete())
                .verifyComplete();
        }

        Assertions.assertEquals(2, flowCalls.get());
        Assertions.assertEquals(prefetch + 10, firstFlow.get());
        Assertions.assertEquals(prefetch + 10, secondFlow.get());
        verify(receiver).closeAsync();
        upstream.assertCancelled();
    }

    @Test
    public void shouldSendCreditOnRequestWhenPrefetchDisabled() {
        final int prefetch = 0;
        final TestPublisher<ReactorReceiver> upstream = TestPublisher.create();
        final MessageFlux messageFlux = new MessageFlux(upstream.flux(), prefetch, retryPolicy);

        final ReactorReceiver receiver = mock(ReactorReceiver.class);
        when(receiver.receive()).thenReturn(Flux.never());
        when(receiver.getEndpointStates()).thenReturn(Flux.just(AmqpEndpointState.ACTIVE));
        when(receiver.closeAsync()).thenReturn(Mono.empty());

        final AtomicInteger flowCalls = new AtomicInteger();
        final AtomicLong firstFlow = new AtomicLong();
        final AtomicLong secondFlow = new AtomicLong();
        final AtomicLong thirdFlow = new AtomicLong();
        final AtomicLong fourthFlow = new AtomicLong();
        doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            @SuppressWarnings("unchecked")
            final Supplier<Long> creditSupplier = (Supplier<Long>) args[0];
            Assertions.assertNotNull(creditSupplier);

            final int calls = flowCalls.incrementAndGet();
            if (calls == 1) {
                firstFlow.set(creditSupplier.get());
            } else if (calls == 2) {
                secondFlow.set(creditSupplier.get());
            } else if (calls == 3) {
                thirdFlow.set(creditSupplier.get());
            } else if (calls == 4) {
                fourthFlow.set(creditSupplier.get());
            }
            return null;
        }).when(receiver).scheduleFlow(any());

        try (VirtualTimeStepVerifier verifier = new VirtualTimeStepVerifier()) {
            verifier.create(() -> messageFlux)
                .then(() -> upstream.next(receiver))
                .thenRequest(10)   //  10  - 0 + 0  = 10 [accumulatedCredit_10 >= 0]
                .thenAwait()
                .thenRequest(20)   //  30  - 10 + 0 = 20 [accumulatedCredit_20 >= 0]
                .thenRequest(30)   //  60  - 30 + 0 = 30 [accumulatedCredit_30 >= 0]
                .thenRequest(40)   //  100 - 60 + 0 = 40 [accumulatedCredit_40 >= 0]
                .then(() -> upstream.complete())
                .verifyComplete();
        }

        Assertions.assertEquals(4, flowCalls.get());
        Assertions.assertEquals(10, firstFlow.get());
        Assertions.assertEquals(20, secondFlow.get());
        Assertions.assertEquals(30, thirdFlow.get());
        Assertions.assertEquals(40, fourthFlow.get());
        verify(receiver).closeAsync();
        upstream.assertCancelled();
    }

    private static List<Message> generateMessages(Message message, int count) {
        return IntStream.rangeClosed(1, count)
            .mapToObj(__ -> message)
            .collect(Collectors.toList());
    }

    // TODO (anu): MoreTests: Evaluate ReceiverLinkProcessor tests and migrate to validate equivalent scenarios (if not covered) using MessageFlux.

    /**
     * An AutoCloseable wrapper over Reactor VirtualTime Verifier.
     */
    private static final class VirtualTimeStepVerifier implements AutoCloseable {
        private final VirtualTimeScheduler scheduler;

        VirtualTimeStepVerifier() {
            scheduler = VirtualTimeScheduler.create();
        }

        <T> StepVerifier.Step<T> create(Supplier<Flux<T>> scenarioSupplier) {
            // VirtualTime Verifier with demand on subscription set to 0.
            return StepVerifier.withVirtualTime(scenarioSupplier, () -> scheduler, 0);
        }

        @Override
        public void close() {
            scheduler.dispose();
        }
    }
}
