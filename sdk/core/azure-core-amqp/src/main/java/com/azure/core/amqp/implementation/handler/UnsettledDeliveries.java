// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation.handler;

import com.azure.core.amqp.AmqpRetryPolicy;
import com.azure.core.amqp.exception.AmqpErrorCondition;
import com.azure.core.amqp.exception.AmqpErrorContext;
import com.azure.core.amqp.exception.AmqpException;
import com.azure.core.amqp.implementation.ExceptionUtil;
import com.azure.core.amqp.implementation.ReactorDispatcher;
import com.azure.core.util.logging.ClientLogger;
import org.apache.qpid.proton.amqp.messaging.Modified;
import org.apache.qpid.proton.amqp.messaging.Outcome;
import org.apache.qpid.proton.amqp.messaging.Rejected;
import org.apache.qpid.proton.amqp.messaging.Released;
import org.apache.qpid.proton.amqp.transaction.TransactionalState;
import org.apache.qpid.proton.amqp.transport.DeliveryState;
import org.apache.qpid.proton.amqp.transport.DeliveryState.DeliveryStateType;
import org.apache.qpid.proton.amqp.transport.ErrorCondition;
import org.apache.qpid.proton.engine.Delivery;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.azure.core.amqp.implementation.ClientConstants.DELIVERY_STATE_KEY;
import static com.azure.core.amqp.implementation.handler.DeliveryHandler.DELIVERY_EMPTY_TAG;
import static com.azure.core.util.FluxUtil.monoError;

/**
 *  Manages the received deliveries which are not settled on the broker. The application can later request settlement
 *  of each delivery by sending a disposition frame with a state representing the desired-outcome,
 *  which the application wishes to occur at the broker. The broker acknowledges this with a disposition frame
 *  with a state (a.k.a. remote-state) representing the actual outcome (a.k.a. remote-outcome) of any work
 *  the broker performed upon processing the settlement request and a flag (a.k.a. remotely-settled) indicating
 *  whether the broker settled the delivery.
 */
public final class UnsettledDeliveries {
    // Ideally value of this const should be 'deliveryTag' but given the only use case today is as Service Bus
    // LockToken, using the value 'lockToken' while logging to ease log parsing.
    // (TODO: anuchan; consider parametrizing the value).
    private static final String DELIVERY_TAG_KEY = "lockToken";
    private final AtomicBoolean isTerminated = new AtomicBoolean();
    private final String hostName;
    private final String entityPath;
    private final String receiveLinkName;
    private final ReactorDispatcher dispatcher;
    private final Duration timeout;
    private final AmqpRetryPolicy retryPolicy;
    private final ClientLogger logger;
    private final Disposable timoutTimer;

    // The deliveries received, for those the application haven't sent disposition frame to the broker requesting
    // settlement or disposition frame is sent, but yet to receive acknowledgment disposition frame from
    // the broker indicating the outcome (a.k.a. remote-outcome).
    private final ConcurrentHashMap<String, Delivery> deliveries = new ConcurrentHashMap<>();
    // A collection of work, where each work representing the disposition frame that the application sent,
    // waiting to receive an acknowledgment disposition frame from the broker indicating the outcome
    // (a.k.a. remote-outcome).
    private final ConcurrentHashMap<String, DispositionWork> pendingDispositions = new ConcurrentHashMap<>();

    /**
     * Creates UnsettledDeliveries.
     *
     * @param hostName the name of the host hosting the messaging entity identified by {@code entityPath}.
     * @param entityPath the relative path identifying the messaging entity from which the deliveries are
     *                   received from, the application can later disposition these deliveries by sending
     *                   disposition frames to the broker.
     * @param receiveLinkName the name of the amqp receive-link 'Attach'-ed to the messaging entity from
     *                       which the deliveries are received from.
     * @param dispatcher the dispatcher to invoke the ProtonJ library API to send disposition frame.
     * @param timeout how long to wait to receive broker's acknowledgment once a disposition frame is sent.
     * @param retryPolicy the retry policy to resend a disposition frame that the broker 'Rejected'.
     * @param logger the logger.
     */
    UnsettledDeliveries(String hostName,
                        String entityPath,
                        String receiveLinkName,
                        ReactorDispatcher dispatcher,
                        Duration timeout,
                        AmqpRetryPolicy retryPolicy,
                        ClientLogger logger) {
        this.hostName = hostName;
        this.entityPath = entityPath;
        this.receiveLinkName = receiveLinkName;
        this.dispatcher = dispatcher;
        this.timeout = timeout;
        this.retryPolicy = retryPolicy;
        this.logger = logger;
        this.timoutTimer = Flux.interval(timeout).subscribe(i -> completeTimedoutDispositionWorks());
    }

    /**
     * Function to notify a received delivery that is unsettled on the broker side; the application can later use
     * {@link UnsettledDeliveries#sendDisposition(String, DeliveryState)} to send a disposition frame requesting
     * settlement of this delivery at the broker.
     *
     * @param deliveryTag the unique delivery tag associated with the {@code delivery}.
     * @param delivery the delivery.
     * @return {@code false} if the instance was closed upon notifying the delivery, {@code true} otherwise.
     */
    boolean onDelivery(UUID deliveryTag, Delivery delivery) {
        if (isTerminated.get()) {
            return false;
        } else {
            // Continue using putIfAbsent as legacy T1 library.
            deliveries.putIfAbsent(deliveryTag.toString(), delivery);
            return true;
        }
    }

    /**
     * Check if a delivery with the given delivery tag was received.
     *
     * @param deliveryTag the delivery tag.
     * @return {@code true} if delivery with the given delivery tag exists {@code false} otherwise.
     */
    boolean containsDelivery(UUID deliveryTag) {
        return deliveryTag != DELIVERY_EMPTY_TAG && deliveries.containsKey(deliveryTag.toString());
    }

    /**
     * Request settlement of delivery (with the unique {@code deliveryTag}) by sending a disposition frame
     * with a state representing the desired-outcome, which the application wishes to occur at the broker.
     * Disposition frame is sent via the same amqp receive-link that delivered the delivery, which was
     * notified through {@link UnsettledDeliveries#onDelivery(UUID, Delivery)}.
     *
     * @param deliveryTag the unique delivery tag identifying the delivery.
     * @param desiredState The state to include in the disposition frame indicating the desired-outcome
     *                    that the application wish to occur at the broker.
     * @return the {@link Mono} upon subscription starts the work by requesting ProtonJ library to send
     * disposition frame to settle the delivery on the broker, and this Mono terminates once the broker
     * acknowledges with disposition frame indicating outcome (a.ka. remote-outcome).
     * The Mono can terminate if the configured timeout elapses or cannot initiate the request to ProtonJ
     * library.
     */
    Mono<Void> sendDisposition(String deliveryTag, DeliveryState desiredState) {
        if (isTerminated.get()) {
            return monoError(logger, new IllegalStateException("Cannot perform updateDisposition on a disposed receiver."));
        } else {
            return sendDispositionImpl(deliveryTag, desiredState);
        }
    }

    /**
     * The function to notify the broker's acknowledgment in response to a disposition frame sent to the broker
     * via {@link UnsettledDeliveries#sendDisposition(String, DeliveryState)}.
     * The broker acknowledgment is also a disposition frame; the ProtonJ library will map this disposition
     * frame to the same Delivery in-memory object for which the application requested disposition.
     * As part of mapping, the remote-state (representing remote-outcome) and is-remotely-settled (boolean)
     * property of the Delivery object is updated from the disposition frame ack.
     *
     * @param deliveryTag the unique delivery tag of the delivery that application requested disposition.
     * @param delivery the delivery object updated from the broker's transfer frame ack.
     */
    void onDispositionAck(UUID deliveryTag, Delivery delivery) {
        final DeliveryState remoteState = delivery.getRemoteState();

        logger.atVerbose()
            .addKeyValue(DELIVERY_TAG_KEY, deliveryTag)
            .addKeyValue(DELIVERY_STATE_KEY, remoteState)
            .log("Received update disposition delivery.");

        final Outcome remoteOutcome;
        if (remoteState instanceof Outcome) {
            remoteOutcome = (Outcome) remoteState;
        } else if (remoteState instanceof TransactionalState) {
            remoteOutcome = ((TransactionalState) remoteState).getOutcome();
        } else {
            remoteOutcome = null;
        }

        if (remoteOutcome == null) {
            logger.atWarning()
                .addKeyValue(DELIVERY_TAG_KEY, deliveryTag)
                .addKeyValue("delivery", delivery)
                .log("No outcome associated with delivery.");

            return;
        }

        final DispositionWork work = pendingDispositions.get(deliveryTag);
        if (work == null) {
            logger.atWarning()
                .addKeyValue(DELIVERY_TAG_KEY, deliveryTag)
                .addKeyValue("delivery", delivery)
                .log("No pending update for delivery.");
            return;
        }

        // the outcome that application desired.
        final DeliveryStateType desiredOutcomeType = work.getDesiredState().getType();
        // the outcome that broker actually attained.
        final DeliveryStateType remoteOutcomeType = remoteState.getType();

        if (desiredOutcomeType == remoteOutcomeType) {
            completeDispositionWork(work, delivery, null);
        } else {
            logger.atInfo()
                .addKeyValue(DELIVERY_TAG_KEY, deliveryTag)
                .addKeyValue("receivedDeliveryState", remoteState)
                .addKeyValue(DELIVERY_STATE_KEY, work.getDesiredState())
                .log("Received delivery state doesn't match expected state.");

            if (remoteOutcomeType == DeliveryStateType.Rejected) {
                handleRetriableRejectedRemoteOutcome(work, delivery, (Rejected) remoteOutcome);
            } else {
                handleReleasedOrUnknownRemoteOutcome(work, delivery, remoteOutcome);
            }
        }
    }

    /**
     * Prepare for the closure of this {@link UnsettledDeliveries}, rejecting further attempts to add
     * deliveries or send delivery dispositions; the preparation phase eagerly timeout any already expired
     * disposition and waits for the completion or timeout of the remaining disposition. Finally, disposes
     * of the timeout timer.
     *
     * @return a {@link Mono} that completes upon the completion of close preparation phase.
     */
    Mono<Void> prepareClose() {
        isTerminated.getAndSet(true);

        // Complete all timed out works then
        completeTimedoutDispositionWorks();

        // wait for the completion of all remaining works (those are not timed out).
        final List<Mono<Void>> workMonoList = new ArrayList<>();
        final StringJoiner deliveryTags = new StringJoiner(", ");
        for (DispositionWork work : pendingDispositions.values()) {
            if (work.hasTimedout()) {
                continue;
            }
            if (work.getDesiredState() instanceof TransactionalState) {
                final Mono<Void> workMono = sendDispositionImpl(work.getDeliveryTag(), Released.getInstance());
                workMonoList.add(workMono);
            } else {
                workMonoList.add(work.getMono());
            }
            deliveryTags.add(work.getDeliveryTag());
        }

        final Mono<Void> workMonoListMerged;
        if (!workMonoList.isEmpty()) {
            logger.info("Waiting for pending updates to complete. Locks: {}", deliveryTags.toString());
            workMonoListMerged = Mono.whenDelayError(workMonoList)
                    .onErrorResume(error -> {
                        logger.info("There was exception(s) while disposing of all disposition work.", error);
                        return Mono.empty();
                    });
        } else {
            workMonoListMerged = Mono.empty();
        }
        return workMonoListMerged.doFinally(__ -> timoutTimer.dispose());
    }

    /**
     * The finalization of this {@link UnsettledDeliveries} closing. Completes any uncompleted work and removes
     * locally unsettled deliveries from the internal list of ProtonJ TransportSession.
     */
    void finishClose() {
        final List<DispositionWork> uncompletedWorks = new ArrayList<>();
        final StringJoiner deliveryTags = new StringJoiner(", ");
        for (DispositionWork work : pendingDispositions.values()) {
            if (work.isCompleted()) {
                continue;
            }
            uncompletedWorks.add(work);
            deliveryTags.add(work.getDeliveryTag());
        }

        if (!uncompletedWorks.isEmpty()) {
            logger.info("Completing {} disposition works as part of receive link termination. Locks: {}",
                uncompletedWorks.size(), deliveryTags.toString());

            final AmqpException completionError = new AmqpException(false,
                "The receiver didn't receive the disposition acknowledgment due to receive link termination.", null);
            for (DispositionWork work : uncompletedWorks) {
                completeDispositionWork(work, null, completionError);
            }

            // The below log can also help debug if the external code that error() calls into never return.
            logger.verbose("Completed {} disposition works as part of receive link termination.", uncompletedWorks.size());
        }

        if (!deliveries.isEmpty()) {
            final Runnable localSettlement = () -> {
                for (Delivery delivery : deliveries.values()) {
                    delivery.disposition(new Modified());
                    delivery.settle();
                }
            };

            try {
                dispatcher.invoke(localSettlement);
            } catch (IOException e) {
                logger.warning("IO sink was closed when scheduling local settlement. Manually settling.", e);
                localSettlement.run();
            } catch (RejectedExecutionException e) {
                logger.info("RejectedExecutionException when scheduling local settlement. Manually settling.", e);
                localSettlement.run();
            }
        }
    }

    /**
     * See the doc for {@link UnsettledDeliveries#sendDisposition(String, DeliveryState)}.
     *
     * @param deliveryTag the unique delivery tag identifying the delivery.
     * @param desiredState The state to include in the disposition frame indicating the desired-outcome
     *                    that the application wish to occur at the broker.
     * @return the {@link Mono} representing disposition work.
     */
    private Mono<Void> sendDispositionImpl(String deliveryTag, DeliveryState desiredState) {
        final Delivery delivery = deliveries.get(deliveryTag);
        if (delivery == null) {
            logger.atWarning()
                .addKeyValue(DELIVERY_TAG_KEY, deliveryTag)
                .log("Delivery not found to update disposition.");

            return monoError(logger, Exceptions.propagate(new IllegalArgumentException(
                "Delivery not on receive link.")));
        }

        final DispositionWork work = new DispositionWork(deliveryTag, desiredState, timeout);

        final Mono<Void> mono = Mono.<Void>create(sink -> {
            work.onStart(sink);
            try {
                dispatcher.invoke(() -> {
                    delivery.disposition(desiredState);
                    pendingDispositions.put(deliveryTag, work);
                });
            } catch (IOException | RejectedExecutionException dispatchError) {
                work.onComplete(new AmqpException(false, "updateDisposition failed while dispatching to Reactor.",
                    dispatchError, getErrorContext(delivery)));
            }
        });

        work.setMono(mono);

        return work.getMono();
    }

    /**
     * Handles the 'Rejected' outcome (in a disposition ack) from the broker in-response to a disposition frame
     * application sent.
     *
     * @param work the work that sent the disposition frame with a desired-outcome which broker 'Rejected'.
     * @param delivery the Delivery in-memory object for which the application had sent the disposition frame;
     *                 the ProtonJ library updates the remote-state (representing remote-outcome) and
     *                 is-remotely-settled (boolean) property of the Delivery object from the disposition frame ack.
     * @param remoteOutcome the 'Rejected' remote-outcome describing the rejection reason, this is derived from
     *                      the remote-state.
     */
    private void handleRetriableRejectedRemoteOutcome(DispositionWork work, Delivery delivery, Rejected remoteOutcome) {
        final AmqpErrorContext amqpErrorContext = getErrorContext(delivery);
        final ErrorCondition errorCondition = remoteOutcome.getError();
        final Throwable error = ExceptionUtil.toException(errorCondition.getCondition().toString(),
            errorCondition.getDescription(), amqpErrorContext);

        final Duration retry = retryPolicy.calculateRetryDelay(error, work.getTryCount());
        if (retry != null) {
            work.onRetriableRejectedOutcome(error);
            try {
                dispatcher.invoke(() -> delivery.disposition(work.getDesiredState()));
            } catch (IOException | RejectedExecutionException dispatchError) {
                final Throwable amqpException = logger.atError()
                    .addKeyValue(DELIVERY_TAG_KEY, work.getDeliveryTag())
                    .log(new AmqpException(false,
                        String.format("linkName[%s], deliveryTag[%s]. Retrying updateDisposition failed to dispatch to Reactor.",
                            receiveLinkName, work.getDeliveryTag()),
                        dispatchError, getErrorContext(delivery)));

                completeDispositionWork(work, delivery, amqpException);
            }
        } else {
            logger.atInfo()
                .addKeyValue(DELIVERY_TAG_KEY, work.getDeliveryTag())
                .addKeyValue(DELIVERY_STATE_KEY, delivery.getRemoteState())
                .log("Retry attempts exhausted.", error);

            completeDispositionWork(work, delivery, error);
        }
    }

    /**
     * Handles the 'Released' or unknown outcome (in a disposition ack) from the broker in-response to a disposition
     * frame application sent.
     *
     * @param work the work that sent the disposition frame with a desired-outcome.
     * @param delivery the Delivery in-memory object for which the application had sent the disposition frame;
     *                the ProtonJ library updates the remote-state (representing remote-outcome) and
     *                is-remotely-settled (boolean) property of the Delivery object from the disposition frame ack.
     * @param remoteOutcome the remote-outcome from the broker describing the reason for broker choosing an outcome
     *                      different from requested desired-outcome, this is derived from the remote-state.
     */
    private void handleReleasedOrUnknownRemoteOutcome(DispositionWork work, Delivery delivery,  Outcome remoteOutcome) {
        final AmqpErrorContext amqpErrorContext = getErrorContext(delivery);
        final AmqpException error;

        final DeliveryStateType remoteOutcomeType = delivery.getRemoteState().getType();
        if (remoteOutcomeType == DeliveryStateType.Released) {
            error = new AmqpException(false, AmqpErrorCondition.OPERATION_CANCELLED,
                "AMQP layer unexpectedly aborted or disconnected.", amqpErrorContext);
        } else {
            error = new AmqpException(false, remoteOutcome.toString(), amqpErrorContext);
        }

        logger.atInfo()
            .addKeyValue(DELIVERY_TAG_KEY, work.getDeliveryTag())
            .addKeyValue(DELIVERY_STATE_KEY, delivery.getRemoteState())
            .log("Completing pending updateState operation with exception.", error);

        completeDispositionWork(work, delivery, error);
    }

    /**
     * Completes the given {@link DispositionWork}, which results in termination of the {@link Mono}
     * returned from the {@link DispositionWork#getMono()} API.
     *
     * @param work the work to complete.
     * @param delivery the delivery that the work attempted the disposition.
     * @param completionError a null value indicates that the work has to complete successfully,
     *                        otherwise complete the work with the error value.
     */
    private void completeDispositionWork(DispositionWork work, Delivery delivery, Throwable completionError) {
        final boolean isRemotelySettled = delivery != null && delivery.remotelySettled();
        if (isRemotelySettled) {
            delivery.settle();
        }

        if (completionError != null) {
            final Throwable loggedError = completionError instanceof RuntimeException
                ? logger.logExceptionAsError((RuntimeException) completionError)
                : completionError;
            work.onComplete(loggedError);
        } else {
            work.onComplete();
        }

        final String deliveryTag = work.getDeliveryTag();
        if (isRemotelySettled) {
            pendingDispositions.remove(deliveryTag);
            deliveries.remove(deliveryTag);
        }
    }

    /**
     * Iterate through all the current {@link DispositionWork} and complete the work those are timed out.
     */
    private void completeTimedoutDispositionWorks() {
        if (pendingDispositions.isEmpty()) {
            return;
        }

        logger.verbose("Cleaning timed out update work tasks.");

        pendingDispositions.forEach((deliveryTag, work) -> {
            if (work == null || !work.hasTimedout()) {
                return;
            }

            pendingDispositions.remove(deliveryTag);
            final Throwable error;
            if (work.getRejectedOutcomeError() != null) {
                error = work.getRejectedOutcomeError();
            } else {
                error = new AmqpException(true, AmqpErrorCondition.TIMEOUT_ERROR,
                    "Update disposition request timed out.", getErrorContext(deliveries.get(deliveryTag)));
            }

            completeDispositionWork(work, null, error);
        });
    }

    /**
     * Gets the error context from the receive-link associated with the delivery.
     *
     * @param delivery the delivery.
     * @return the error context from delivery's receive-link, {@code null} if the delivery or
     * receive-link is {@code null}.
     */
    private AmqpErrorContext getErrorContext(Delivery delivery) {
        if (delivery == null || delivery.getLink() == null) {
            return null;
        }
        return LinkHandler.getErrorContext(hostName, entityPath, delivery.getLink());
    }

    /**
     * Represents a work that, upon starting, requests ProtonJ library to send a disposition frame to settle
     * a delivery on the broker and the work completes when the broker acknowledges with a disposition frame
     * indicating the outcome. The work can complete with an error if it cannot initiate the request
     * to the ProtonJ library or the configured timeout elapses.
     * <p/>
     * The work is started once the application is subscribed to the {@link Mono} returned by
     * {@link DispositionWork#getMono()}; the Mono is terminated upon the work completion.
     */
    private static final class DispositionWork extends AtomicBoolean {
        private final AtomicInteger tryCount = new AtomicInteger(1);
        private final String deliveryTag;
        private final DeliveryState desiredState;
        private final Duration timeout;
        private Mono<Void> mono;
        private MonoSink<Void> monoSink;
        private Instant expirationTime;
        private Throwable rejectedOutcomeError;

        /**
         * Create a DispositionWork.
         *
         * @param deliveryTag The delivery tag of the Delivery for which to send the disposition frame requesting
         *                   delivery settlement on the broker.
         * @param desiredState The state to include in the disposition frame indicating the desired-outcome
         *                    the application wish to occur at the broker.
         * @param timeout after requesting the ProtonJ library to send the disposition frame, how long to wait for
         *                an acknowledgment disposition frame to arrive from the broker.
         */
        DispositionWork(String deliveryTag, DeliveryState desiredState, Duration timeout) {
            this.deliveryTag = deliveryTag;
            this.desiredState = desiredState;
            this.timeout = timeout;
        }

        /**
         * Gets the delivery tag.
         *
         * @return the delivery tag.
         */
        String getDeliveryTag() {
            return deliveryTag;
        }

        /**
         * Gets the state indicating the desired-outcome which the application wishes to occur at the broker.
         * The disposition frame send to the broker includes this desired state.
         *
         * @return the desired state.
         */
        DeliveryState getDesiredState() {
            return desiredState;
        }

        /**
         * Gets the number of times the work was tried.
         *
         * @return the try count.
         */
        int getTryCount() {
            return tryCount.get();
        }

        /**
         * Gets the error received from the broker when the outcome of the last disposition attempt
         * (by sending a disposition frame) happened to be 'Rejected'.
         *
         * @return the error in the disposition ack frame from the broker with 'Rejected' outcome,
         * null if no such disposition ack frame received.
         */
        Throwable getRejectedOutcomeError() {
            return rejectedOutcomeError;
        }

        /**
         * Check if the work has timed out.
         *
         * @return {@code true} if the work has timed out, {@code false} otherwise.
         */
        boolean hasTimedout() {
            return expirationTime != null && expirationTime.isBefore(Instant.now());
        }

        /**
         * Gets the {@link Mono} upon subscription starts the work by requesting ProtonJ library to send
         * disposition frame to settle a delivery on the broker, and this Mono terminates once the broker
         * acknowledges with disposition frame indicating settlement outcome (a.k.a. remote-outcome)
         * The Mono can terminate if the configured timeout elapses or cannot initiate the request to
         * ProtonJ library.
         *
         * @return the mono
         */
        Mono<Void> getMono() {
            return mono;
        }

        /**
         * Sets the {@link Mono}, where the application can obtain cached version of it
         * from {@link DispositionWork#getMono()} and subscribe to start the work, the mono terminates
         * upon the successful or unsuccessful completion of the work.
         *
         * @param mono the mono
         */
        void setMono(Mono<Void> mono) {
            // cache() the mono to replay the result when subscribed more than once, avoid multiple
            // disposition placement (and enables a possible second subscription to be safe when closing
            // the UnsettledDeliveries type).
            this.mono = mono.cache();
        }

        /**
         * Check if this work is already completed.
         *
         * @return {@code true} if the work is completed, {@code true} otherwise.
         */
        boolean isCompleted() {
            return this.get();
        }

        /**
         * The function invoked once the application start the work by subscribing to the {@link Mono}
         * obtained from {@link DispositionWork#getMono()}.
         *
         * @param monoSink the {@link MonoSink} to notify the completion of the work, which triggers
         * termination of the same {@link Mono} that started the work.
         */
        void onStart(MonoSink<Void> monoSink) {
            this.monoSink = monoSink;
            expirationTime = Instant.now().plus(timeout);
        }

        /**
         * The function invoked when the work is about to be restarted/retried. The broker may return an
         * outcome named 'Rejected' if it is unable to attain the desired-outcome that the application
         * specified in the disposition frame; in this case, the work is retried based on the configured
         * retry settings.
         *
         * @param error the error that the broker returned upon Reject-ing the last work execution attempting
         *             the disposition.
         */
        void onRetriableRejectedOutcome(Throwable error) {
            this.rejectedOutcomeError = error;
            expirationTime = Instant.now().plus(timeout);
            tryCount.incrementAndGet();
        }

        /**
         * the function invoked upon the successful completion of the work.
         */
        void onComplete() {
            this.set(true);
            monoSink.success();
        }

        /**
         * the function invoked when the work is completed with an error.
         *
         * @param error the error reason.
         */
        void onComplete(Throwable error) {
            this.set(true);
            monoSink.error(error);
        }
    }
}
