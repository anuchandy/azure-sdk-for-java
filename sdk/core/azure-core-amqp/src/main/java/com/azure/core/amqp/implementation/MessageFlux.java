// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

import com.azure.core.amqp.AmqpEndpointState;
import com.azure.core.amqp.AmqpRetryPolicy;
import com.azure.core.util.AsyncCloseable;
import com.azure.core.util.logging.ClientLogger;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.message.Message;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxOperator;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static com.azure.core.amqp.implementation.ClientConstants.ENTITY_PATH_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.LINK_NAME_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.SUBSCRIBER_ID_KEY;

/**
 * The Flux operator to stream messages reliably from a messaging entity (e.g., Event Hub, Service Bus Queue)
 * to downstream subscriber.
 */
public final class MessageFlux extends FluxOperator<ReactorReceiver, Message> {
    private final int prefetch;
    private final AmqpRetryPolicy retryPolicy;

    /**
     * Create a message-flux to stream messages from a messaging entity to downstream subscriber.
     *
     * @param source the upstream source that, upon request, provide receivers connected to the messaging entity.
     * @param prefetch the number of messages that the operator should prefetch from the messaging entity (for a
     *                 less chatty network and faster message processing on the client).
     * @param retryPolicy the retry policy to use to recover from receiver termination.
     */
    protected MessageFlux(Flux<? extends ReactorReceiver> source,
                          int prefetch,
                          AmqpRetryPolicy retryPolicy) {
        super(source);
        if (prefetch < 0) {
            throw new IllegalArgumentException("prefetch >= 0 required but it was " + prefetch);
        }
        this.prefetch = prefetch;
        this.retryPolicy = Objects.requireNonNull(retryPolicy, "'retryPolicy' cannot be null.");
    }

    /**
     * Register the downstream subscriber.
     *
     * @param actual the downstream subscriber interested in the published messages and termination.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void subscribe(CoreSubscriber<? super Message> actual) {
        source.subscribe(new RecoverableReactorReceiver((CoreSubscriber<Message>) actual, prefetch, retryPolicy));
    }

    /**
     * The underlying consumer and producer extension of the message-flux operator. The consuming side processes
     * events from the upstream (which provides receivers) and current receiver (which provide messages),
     * and producing side publishes the events to the downstream. The type has recovery mechanism to create a new
     * receiver upon the current receiver's termination; recoveries happen underneath while the messages flow
     * transparently downstream.
     */
    private static final class RecoverableReactorReceiver implements CoreSubscriber<ReactorReceiver>, Subscription {
        // A flag indicates if the downstream termination due to the upstream signal for operator completion
        // needs to wait for the current mediator to terminate (experimental, disabled internally).
        private  final boolean completeAfterMediatorFlush = false;
        // holds the current mediator that coordinates between a receiver and the recoverable-receiver.
        private final MediatorHolder mediatorHolder = new MediatorHolder();
        private final ClientLogger logger;
        private final int prefetch;
        private final AmqpRetryPolicy retryPolicy;
        private final AtomicInteger retryAttempts = new AtomicInteger();
        private final CoreSubscriber<Message> messageSubscriber;
        volatile Throwable error;
        @SuppressWarnings("rawtypes")
        static final AtomicReferenceFieldUpdater<RecoverableReactorReceiver, Throwable> ERROR =
            AtomicReferenceFieldUpdater.newUpdater(RecoverableReactorReceiver.class,
                Throwable.class,
                "error");
        private volatile boolean done;
        private volatile boolean cancelled;
        private Subscription upstream;
        private volatile long requested;
        @SuppressWarnings("rawtypes")
        private static final AtomicLongFieldUpdater<RecoverableReactorReceiver> REQUESTED =
            AtomicLongFieldUpdater.newUpdater(RecoverableReactorReceiver.class, "requested");
        private volatile int wip;
        @SuppressWarnings("rawtypes")
        private static final AtomicIntegerFieldUpdater<RecoverableReactorReceiver> WIP =
            AtomicIntegerFieldUpdater.newUpdater(RecoverableReactorReceiver.class, "wip");

        /**
         * Create a recoverable-receiver to support the message-flux to stream messages from a messaging entity
         * to downstream subscriber.
         *
         * @param messageSubscriber the downstream subscriber to notify the events (messages, termination).
         * @param prefetch the number of messages that the operator should prefetch from the messaging entity
         *                 (for a less chatty network and faster message processing on the client).
         * @param retryPolicy the retry policy to use to recover from receiver termination.
         */
        RecoverableReactorReceiver(CoreSubscriber<Message> messageSubscriber,
                                   int prefetch,
                                   AmqpRetryPolicy retryPolicy) {
            this.messageSubscriber = messageSubscriber;
            this.prefetch = prefetch;
            this.retryPolicy = retryPolicy;

            final String subscriberId = StringUtil.getRandomString("rsbr");
            final Map<String, Object> loggingContext = new HashMap<>(1);
            loggingContext.put(SUBSCRIBER_ID_KEY, subscriberId);
            this.logger = new ClientLogger(RecoverableReactorReceiver.class, loggingContext);
        }

        /**
         * Invoked by the upstream in-response to the subscription to message-flux operator.
         *
         * @param s the subscription that the operator uses to request receivers from the upstream
         *         or terminate upstream through cancellation when it is no longer needed.
         */
        @Override
        public void onSubscribe(Subscription s) {
            if (Operators.validate(this.upstream, s)) {
                this.upstream = s;
                messageSubscriber.onSubscribe(this);
                // Request the first receiver from the upstream.
                s.request(1);
            }
        }

        /**
         * Invoked by the upstream to deliver new receiver.
         *
         * @param receiver the new receiver.
         */
        @Override
        public void onNext(ReactorReceiver receiver) {
            if (done) {
                receiver.closeAsync().subscribe();
                Operators.onNextDropped(receiver, messageSubscriber.currentContext());
                return;
            }

            // Create a new mediator to channel communication between the new receiver and the recoverable-receiver.
            final ReactorReceiverMediator mediator = new ReactorReceiverMediator(this, receiver, prefetch);

            // Request MediatorHolder to set the new mediator as the current (for the drain-loop to pick)
            if (mediatorHolder.trySet(mediator)) {
                // the MediatorHolder accepted the mediator. Notify the mediator that the recoverable-receiver
                // (a.k.a parent) is ready to use the mediator; in-response, the mediator notifies its readiness
                // by invoking 'onMediatorReady'.
                mediator.onParentReady();
            } else {
                // the MediatorHolder rejected the mediator as holder was frozen due to operator cancellation or
                // termination.
                logger.atWarning()
                    .addKeyValue("oldLinkName", mediatorHolder.getLinkName())
                    .addKeyValue(LINK_NAME_KEY, receiver.getLinkName())
                    .addKeyValue(ENTITY_PATH_KEY, receiver.getEntityPath())
                    .log("Got a receiver when RecoverableServiceBusReactiveReceiver is already terminated.");
                receiver.closeAsync().subscribe();
                Operators.onDiscard(receiver, messageSubscriber.currentContext());
            }
        }

        /**
         * Invoked by the upstream to signal operator termination with an error or invoked from the drain-loop
         * to signal operator termination due to retry exhaust error.
         *
         * @param e the error signaled.
         */
        @Override
        public void onError(Throwable e) {
            if (done) {
                Operators.onErrorDropped(e, messageSubscriber.currentContext());
                return;
            }

            // It is possible that the upstream error and retry exhaust error signals concurrently; if so,
            // a CompositeException object holds both errors.
            if (Exceptions.addThrowable(ERROR, this, e)) {
                done = true;
                drain(null);
            } else {
                // If the drain-loop processed the last error, then further errors dropped through the standard
                // Reactor channel. E.g., retry exhaust error happened and, as part of its processing, upstream
                // gets canceled, but if upstream still emits an error, then it gets dropped.
                Operators.onErrorDropped(e, messageSubscriber.currentContext());
            }
        }

        /**
         * Invoked by the upstream to signal operator termination with completion.
         */
        @Override
        public void onComplete() {
            if (done) {
                return;
            }

            done = true;
            drain(null);
        }

        /**
         * Invoked by the downstream to signal the demand for messages. Whatever has been requested can be sent,
         * so only signal the demand for what can be safely handled. No messages will be sent downstream until
         * the demand is signaled.
         *
         * @param n the number of messages to send to downstream.
         */
        @Override
        public void request(long n) {
            if (Operators.validate(n)) {
                Operators.addCap(REQUESTED, this, n);
                drain(null);
            }
        }

        /**
         * Invoked by downstream to signal termination of operator by cancellation.
         */
        @Override
        public void cancel() {
            if (cancelled) {
                return;
            }

            cancelled = true;
            // By incrementing wip, signal the active drain-loop about the cancel,
            if (WIP.getAndIncrement(this) == 0) {
                // but signaling identified no active drain-loop; hence immediately react to cancel.
                upstream.cancel();
                mediatorHolder.freeze();
                // wip increment also placed a tombstone on the drain-loop, so further drain(..) calls are nop.
            }
        }

        /**
         * Invoked once the new mediator initialized in the 'onNext' is ready to use as a result of its
         * backing receiver readiness.
         */
        void onMediatorReady() {
            retryAttempts.set(0);
            // After invoking 'messageSubscriber.onSubscribe(subscription)' and before the readiness
            // of the mediator, there may be request signals for messages from the downstream, invoke
            // drain(...) to process those signals.
            drain(null);
        }

        /**
         * The serialized entry point to drain-loop.
         *
         * @param dataSignal the message to drop if the operator is terminated by cancellation.
         */
        void drain(Message dataSignal) {
            // By incrementing wip, signal the drain-loop about readiness of the new mediator, the arrival
            // of message or operator termination by the upstream,
            if (WIP.getAndIncrement(this) != 0) {
                if (dataSignal != null && cancelled) {
                    // but signaling identified that the tombstone is placed on the drain-loop, so drop the
                    // data signal, if any (e.g., message), through the standard Reactor channel.
                    Operators.onDiscard(dataSignal, messageSubscriber.currentContext());
                }
                return;
            }
            drainLoop();
        }

        /**
         * The serialized drain-loop (implementation patterns inspired from RxJava, Reactor Operators).
         * Reference: 'Operator Concurrency Primitives' series https://akarnokd.blogspot.com/2015/05/
         */
        private void drainLoop() {
            int missed = 1;
            CoreSubscriber<Message> downstream = this.messageSubscriber;
            // Begin: serialized drain-loop.
            for (; ;) {
                boolean d = done;
                // Obtain the current mediator (backed by a receiver)
                ReactorReceiverMediator mediator = mediatorHolder.mediator;
                // the mediator can be null only if the upstream signals operator termination without emitting
                // the very first receiver, hence no associated mediator.
                boolean hasMediator = mediator != null && !mediatorHolder.noNextMediator;

                if (terminateIfCancelled(downstream, null)) {
                    // the 'return' from the drain-loop in response to 'true' from 'terminateIf*' methods
                    // places a tombstone on the drain-loop (by not reducing the wip counter).
                    // Once tombstone placed on the drain-loop, further drain(..) calls are nop.
                    return;
                }

                if (terminateIfErroredOrUpstreamCompleted(d, hasMediator, downstream, null)) {
                    return;
                }

                long r = this.requested;
                long emitted = 0L;
                boolean requestNextMediator = false;

                if (r != 0L && hasMediator) {
                    // there is demand ('r' != 0) from the downstream; see if it can be satisfied.
                    Queue<Message> q = mediator.queue;
                    // Begin: emitter-loop.
                    // Emits up to 'r' (requested) messages to the downstream if available in the mediator's queue,
                    // i.e., emitter-loop emits min(r, queue.size) messages.
                    while (emitted != r) {
                        Message message = q.poll();

                        if (terminateIfCancelled(downstream, message)) {
                            return;
                        }

                        if (terminateIfErroredOrUpstreamCompleted(done, true, downstream, message)) {
                            return;
                        }

                        boolean empty = message == null;
                        // check if a new mediator may be needed.
                        if (empty && mediator.done) {
                            // Emitted all messages from the current mediator, and its backing receiver termination
                            // terminated the mediator; we may obtain a new mediator with a new backing receiver.
                            requestNextMediator = true;
                            break;
                        }

                        if (empty) {
                            // There were more requested, but no messages left in the mediator's queue.
                            break;
                        }

                        messageSubscriber.onNext(message);
                        emitted++;
                    }
                    // End: emitter-loop.

                    if (emitted == r) {
                        // The emitter-loop checks the need for a new mediator until 'r-1' emissions; let's check
                        // after the 'r'-th emission.
                        if (mediator.queue.isEmpty() && mediator.done) {
                            requestNextMediator = true;
                        }
                    }

                    if (emitted != 0 && r != Long.MAX_VALUE) {
                        r = REQUESTED.addAndGet(this, -emitted);
                    }
                    mediator.update(r, emitted); // TODO (anu): max-value request mapping before 'update(..)'.
                }

                if (r == 0L && hasMediator) {
                    // Even if there was no downstream demand (i.e., 'r' == 0),

                    // there could be pending cancellation signal from the downstream for the operator termination
                    if (terminateIfCancelled(downstream, null)) {
                        return;
                    }

                    // or pending signal from the upstream to terminate the operator with error or completion
                    // or last drain-loop iteration detected retry exhaust needing operator termination with error,
                    if (terminateIfErroredOrUpstreamCompleted(done, true, downstream, null)) {
                        return;
                    }

                    // or pending signal indicating the mediator termination. E.g., receiver detached without
                    // receiving a message.
                    if (mediator.queue.isEmpty() && mediator.done) {
                        requestNextMediator = true;
                    }
                }

                if (requestNextMediator) {
                    // The current mediator's queue is drained, and the mediator is terminated, let's close it,
                    mediator.closeAsync().subscribe();
                    // and proceed with obtaining a new mediator.
                    tryRequestNextMediator(mediator.error, mediatorHolder);
                }

                missed = WIP.addAndGet(this, -missed);
                if (missed == 0) {
                    break;
                }
                // The next serialized drain-loop iteration to process missed signals arrived during last iteration.
            }
            // End: serialized drain-loop.
        }

        /**
         * CONTRACT: Never invoke from the outside of serialized drain-loop.
         * <br/>
         * See if downstream signaled cancellation to terminate the operator, if so, react to the cancellation.
         *
         * @param downstream the downstream.
         * @param messageDropped the message that gets dropped if cancellation was signaled.
         * @return true if canceled, false otherwise.
         */
        private boolean terminateIfCancelled(CoreSubscriber<Message> downstream, Message messageDropped) {
            if (cancelled) {
                Operators.onDiscard(messageDropped, downstream.currentContext());
                upstream.cancel();
                mediatorHolder.freeze();
                return true;
            }
            return false;
        }

        /**
         * CONTRACT: Never invoke from the outside of serialized drain-loop.
         * <br/>
         * See if the upstream signaled the operator termination with error or completion or drain-loop detected
         * retry exhaust needing operator termination with error; if so, react to it by terminating downstream.
         *
         * @param d indicate if the operator termination was signaled.
         * @param hasMediator indicate there is no active mediator and there won't be one in the future.
         * @param downstream the downstream.
         * @param messageDropped the message that gets dropped if termination happened.
         * @return true if terminated, false otherwise.
         */
        private boolean terminateIfErroredOrUpstreamCompleted(boolean d, boolean hasMediator,
                                                              CoreSubscriber<Message> downstream,
                                                              Message messageDropped) {
            if (d) {
                // true for 'd' means the operator termination was signaled
                Throwable e = error;
                if (e != null && e != Exceptions.TERMINATED) {
                    // A non-null 'e' indicates upstream signaled operator termination with an error, or there is
                    // non-retriable or retry exhausted error; let's terminate the local resources
                    // (canceling upstream, freezing 'e' by marking it as TERMINATED, freezing mediator-holder)
                    // and propagate the error to terminate the downstream.
                    e = Exceptions.terminate(ERROR, this);
                    Operators.onDiscard(messageDropped, downstream.currentContext());
                    upstream.cancel();
                    mediatorHolder.freeze();
                    downstream.onError(e);
                    return true;
                }
                // The absence of error indicates upstream signaled operator termination with completion; downstream
                // completion can be delayed until the current mediator's queue is drained and terminated,
                // or downstream completion can be done eagerly.
                if (completeAfterMediatorFlush) {
                    // The termination of the downstream with completion should happen only after the current mediator
                    // is drained and terminated.
                    if (!hasMediator) {
                        // No mediator indicates, had there a mediator, it will be now drained and terminated.
                        downstream.onComplete();
                        return true;
                    }
                } else {
                    Operators.onDiscard(messageDropped, downstream.currentContext());
                    upstream.cancel();
                    mediatorHolder.freeze();
                    downstream.onComplete();
                    return true;
                }
            }
            return false;
        }

        /**
         * CONTRACT: Never invoke from the outside of serialized drain-loop.
         * <br/>
         * Request the next mediator if the operator is not in a termination signaled state and if the retry
         * is not exhausted. If there is a non-retriable or retry is exhaust error, then proceed with error-ed
         * termination of the operator.
         *
         * @param error the error that leads to error-ed termination of the last mediator or {@code null}
         *              if terminated with completion.
         * @param mediatorHolder the mediator holder.
         */
        private void tryRequestNextMediator(Throwable error, MediatorHolder mediatorHolder) {
            if (cancelled || done) {
                // To terminate the operator, the downstream signaled cancellation or upstream signaled error
                // or completion. The next drain-loop iteration as a result of that signalling terminate
                // the operator through one of the 'terminateIf*' methods followed by placing tombstone on
                // the drain-loop.

                // Below flag indicates there is no mediator currently, and there won't be one in the future,
                // which shows the current mediator is flushed. When 'completeAfterMediatorFlush' is set
                // and upstream signals operator completion, the flag 'noNextMediator' tells the next drain-loop
                // iteration that it can terminate downstream gracefully.
                mediatorHolder.noNextMediator = true;
                return;
            }

            final Duration delay;
            if (error == null) {
                // TODO (anu): decide the back-off duration when no error.
                delay = Duration.ofSeconds(1);
            } else {
                // error != null
                final int attempt = retryAttempts.incrementAndGet();
                delay = retryPolicy.calculateRetryDelay(error, attempt);
                if (delay == null) {
                    // TODO (anu): log non-retriable or retry exhausted error
                    // Invoking 'onError' to signal the next drain-loop iteration to terminate the operator
                    // with 'error',
                    onError(error);
                    // once the control returns below, the next immediate drain-loop iteration (iteration
                    // guaranteed by the above signaling) picks the signal, terminates the operator
                    // through 'terminateIfErrored*' method and places tombstone on the drain-loop.
                    return;
                }
            }

            // TODO (anu): log retry attempt

            mediatorHolder.nextMediatorRequestDisposable = Schedulers.boundedElastic().schedule(() -> {
                if (cancelled) {
                    // The downstream signaled cancellation to terminate the operator before the retry
                    // back-off expiration.
                    return;
                }
                if (done) {
                    // The upstream signaled operator termination before the retry back-off expiration.
                    if (completeAfterMediatorFlush) {
                        mediatorHolder.noNextMediator = true;
                        drain(null);
                    }
                    return;
                }
                // request upstream for a new receiver so that the resulting receiver can back new mediator.
                upstream.request(1);
            }, delay.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * The mediator that coordinates between {@link RecoverableReactorReceiver} and a {@link ReactorReceiver}.
     */
    private static final class ReactorReceiverMediator implements AsyncCloseable, CoreSubscriber<Message>, Subscription {
        private static final Message TERMINAL_MESSAGE = Proton.message();
        private final RecoverableReactorReceiver parent;
        private final ReactorReceiver receiver;
        private final int prefetch;
        private Disposable endpointStateDisposable;
        private volatile boolean ready;
        private CreditAccounting creditAccounting;

        private volatile Subscription s;
        @SuppressWarnings("rawtypes")
        private static final AtomicReferenceFieldUpdater<ReactorReceiverMediator, Subscription> S =
            AtomicReferenceFieldUpdater.newUpdater(ReactorReceiverMediator.class,
                Subscription.class,
                "s");
        volatile Throwable error;
        @SuppressWarnings("rawtypes")
        static final AtomicReferenceFieldUpdater<ReactorReceiverMediator, Throwable> ERROR =
            AtomicReferenceFieldUpdater.newUpdater(ReactorReceiverMediator.class,
                Throwable.class,
                "error");
        volatile boolean done;
        final Queue<Message> queue;

        /**
         * Create a mediator to channel events (messages, termination) from a receiver to recoverable-receiver.
         *
         * @param parent the recoverable-receiver (a.k.a. parent).
         * @param receiver the receiver backing the mediator.
         * @param prefetch the number of messages to prefetch using the receiver (for a less chatty network
         *                 and faster message processing on the client).
         */
        ReactorReceiverMediator(RecoverableReactorReceiver parent, ReactorReceiver receiver, int prefetch) {
            this.parent = parent;
            this.receiver = receiver;
            this.prefetch = prefetch;
            // Obtain a resizable single-producer & single-consumer queue (SpscLinkedArrayQueue).
            this.queue = Queues.<Message>get(Integer.MAX_VALUE).get();
        }

        /**
         * Invoked by the recoverable-receiver (a.k.a. parent) when it is ready to use the mediator.
         */
        void onParentReady() {
            // Subscribe for messages and terminal events from the receiver.
            receive(receiver).subscribe(this);
            // Subscribe to the receiver's endpoint state so that the mediator can let the recoverable-receiver
            // (a.k.a. parent) know its readiness once the receiver is active.
            endpointStateDisposable = receiver.getEndpointStates()
                .publishOn(Schedulers.boundedElastic())
                .subscribe(state -> {
                    if (state == AmqpEndpointState.ACTIVE) {
                        ready = true;
                        parent.onMediatorReady();
                    }
                });
        }

        /**
         * Invoked in response to the subscription to the receiver's message publisher.
         *
         * @param s the subscription to request messages from the receiver's message publisher and terminate
         *         that publisher through cancellation when it is no longer needed.
         */
        @Override
        public void onSubscribe(Subscription s) {
            if (Operators.setOnce(S, this, s)) {
                creditAccounting = new CreditAccounting(receiver, s, prefetch);
            }
        }

        /**
         * CONTRACT: Never invoke from the outside of serialized drain-loop.
         * <br/>
         * Notify the latest view of the downstream request and messages emitted by the emitter-loop during
         * the last drain-loop iteration.
         *
         * @param request the latest view of the downstream request.
         * @param emitted the number of messages emitted by the latest emitter-loop run.
         */
        void update(long request, long emitted) {
            if (ready && !done) {
                creditAccounting.update(request, emitted);
            }
        }

        /**
         * Invoked by the receiver's message publisher to deliver a message.
         *
         * @param message the message.
         */
        @Override
        public void onNext(Message message) {
            if (done) {
                Operators.onNextDropped(message, parent.currentContext());
                return;
            }

            if (s == Operators.cancelledSubscription()) {
                Operators.onDiscard(message, parent.currentContext());
                return;
            }

            if (queue.offer(message)) {
                parent.drain(message);
            } else {
                Operators.onOperatorError(this,
                    Exceptions.failWithOverflow(Exceptions.BACKPRESSURE_ERROR_QUEUE_FULL),
                    parent.messageSubscriber.currentContext());
                Operators.onDiscard(message, parent.messageSubscriber.currentContext());
                done = true;
                // At this point "this.s == canceled, isEmpty(this.queue) == true, this.done == true".
                // this.s == cancelled means upstream 'receiver' subscription is canceled. Even if the 'receiver'
                // invokes this.onNext(m) a few times, they are all discarded keeping queue empty. When drain-loop
                // iteration sees this.done && isEmpty(this.queue) == true, the mediator is closed and proceeds to
                // request the next mediator.
                parent.drain(message);
            }
        }

        /**
         * Invoked by the receiver's message publisher to signal mediator termination with an error.
         *
         * @param e the error signaled.
         */
        @Override
        public void onError(Throwable e) {
            if (done) {
                Operators.onErrorDropped(e, parent.messageSubscriber.currentContext());
                return;
            }

            if (ERROR.compareAndSet(this, null, e)) {
                done = true;
                parent.drain(null);
            } else {
                done = true;
                Operators.onErrorDropped(e, parent.messageSubscriber.currentContext());
            }
        }

        /**
         * Invoked by the receiver's message publisher to signal mediator termination with completion.
         */
        @Override
        public void onComplete() {
            if (done) {
                return;
            }

            done = true;
            parent.drain(null);
        }

        @Override
        public void request(long n) {
            throw new IllegalStateException("The request accounting must be through update(,).");
        }

        /**
         * Invoked when the recoverable-receiver wants to terminate the mediator (and backing receiver's
         * message publisher) by cancellation.
         */
        @Override
        public void cancel() {
            Operators.terminate(S, this);
            Operators.onDiscardQueueWithClear(queue, parent.currentContext(), null);
        }

        @Override
        public Mono<Void> closeAsync() {
            if (endpointStateDisposable != null) {
                endpointStateDisposable.dispose();
            }
            return receiver.closeAsync();
        }

        /**
         * Obtain the Flux that streams messages and terminal events from the given receiver.
         *
         * @param receiver the receiver.
         * @return the Flux.
         */
        private static Flux<Message> receive(ReactorReceiver receiver) {
            // The error is delivered through the endpoint-states Flux, so subscribe to endpoint-states
            // Flux once the message publisher Flux terminates.
            return receiver.receive()
                .concatWith(receiver.getEndpointStates()
                    .then(Mono.just(TERMINAL_MESSAGE))
                    .filter(m -> m == TERMINAL_MESSAGE));
        }

        /**
         * The type tracking the credits for the receiver backing the mediator. It decides when to request messages
         * to the receiver's message publisher and send the credits to the broker.
         */
        private static final class CreditAccounting {
            private final ReactorReceiver receiver;
            private final Subscription subscription;
            private final int prefetch;
            private long pendingMessageCount;
            private final AtomicLong accumulatedCredit = new AtomicLong(0);

            /**
             * Create new CreditAccounting to track credit associated with a receiver backing a mediator.
             *
             * @param receiver the receiver for sending credit to the broker.
             * @param subscription the subscription to the receiver's message publisher to request messages when
             *                    needed (the publisher won't translate these requests to network credit flow).
             * @param prefetch the prefetch configured in the mediator.
             */
            CreditAccounting(ReactorReceiver receiver, Subscription subscription, int prefetch) {
                this.receiver = receiver;
                this.subscription = subscription;
                this.prefetch = prefetch;
                this.pendingMessageCount = 0;
            }

            /**
             * Notify the latest view of the downstream request and messages emitted by the emitter-loop during
             * the last drain-loop iteration.
             *
             * @param request the latest view of the downstream request.
             * @param emitted the number of messages emitted by the latest emitter-loop run.
             */
            void update(long request, long emitted) {
                pendingMessageCount -= emitted;
                final long c = request - pendingMessageCount + prefetch;
                if (c > 0) {
                    pendingMessageCount += c;
                    subscription.request(c);
                    if (accumulatedCredit.addAndGet(c) >= prefetch) {
                        // TODO: Try-Catch-Log
                        receiver.scheduleFlow(() -> accumulatedCredit.getAndSet(0));
                    }
                }
            }
        }
    }

    /**
     * A type that supports atomically setting a mediator and disposing of the last set mediator upon freezing.
     * Once frozen, further attempt to set the mediator will be rejected. The object of this type holds
     * the current mediator that the drain-loop access to read events from the receiver (backing the mediator).
     */
    private static final class MediatorHolder {
        private boolean isFrozen;
        volatile boolean noNextMediator;
        volatile ReactorReceiverMediator mediator;
        // Holds the subscription to the task that, when executed, request the next mediator.
        volatile Disposable nextMediatorRequestDisposable;

        /**
         * Try to set the current mediator for the drain-loop.
         *
         * @param mediator the mediator.
         * @return true if the mediator is set successfully, false if the attempt to set is rejected due
         * to the holder in the frozen state.
         */
        boolean trySet(ReactorReceiverMediator mediator) {
            synchronized (this) {
                if (isFrozen) {
                    return false;
                }
                this.mediator = mediator;
                return true;
            }
        }

        /**
         * Freeze the holder to dispose of the current mediator and any resources it tracks; no further
         * mediator can be set once frozen. Freezing happens when the message-flux operator is terminated.
         */
        void freeze() {
            final Disposable d;
            final ReactorReceiverMediator m;
            synchronized (this) {
                if (isFrozen) {
                    return;
                }
                d = nextMediatorRequestDisposable;
                m = this.mediator;
                isFrozen = true;
            }

            if (d != null) {
                d.dispose();
            }
            if (m != null) {
                m.cancel();
                m.closeAsync().subscribe();
            }
        }

        String getLinkName() {
            final ReactorReceiverMediator m = mediator;
            return m != null ? m.receiver.getLinkName() : null;
        }

        String getEntityPath() {
            final ReactorReceiverMediator m = mediator;
            return m != null ? m.receiver.getEntityPath() : null;
        }
    }
}
