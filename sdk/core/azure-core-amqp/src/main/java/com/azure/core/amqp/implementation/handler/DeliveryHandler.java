// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation.handler;

import com.azure.core.amqp.AmqpRetryPolicy;
import com.azure.core.amqp.implementation.ReactorDispatcher;
import com.azure.core.util.logging.ClientLogger;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.messaging.Accepted;
import org.apache.qpid.proton.amqp.messaging.Modified;
import org.apache.qpid.proton.amqp.transport.DeliveryState;
import org.apache.qpid.proton.amqp.transport.ErrorCondition;
import org.apache.qpid.proton.engine.Delivery;
import org.apache.qpid.proton.engine.EndpointState;
import org.apache.qpid.proton.engine.Link;
import org.apache.qpid.proton.engine.Receiver;
import org.apache.qpid.proton.message.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.azure.core.amqp.implementation.AmqpLoggingUtils.addErrorCondition;
import static com.azure.core.amqp.implementation.ClientConstants.EMIT_RESULT_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.ENTITY_PATH_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.LINK_NAME_KEY;

/**
 * A type to handle all {@link Delivery} from the ProtonJ library. The ProtonJ library creates {@link Delivery}
 * object upon receiving a transfer or disposition frame from the broker.
 * <p>
 * This type takes care of streaming the {@link Message} objects read and decoded from the transfer frame based
 * {@link Delivery} objects and enables settlement of those deliveries.
 */
final class DeliveryHandler {
    static final UUID DELIVERY_EMPTY_TAG = new UUID(0L, 0L);
    private static final int DELIVERY_TAG_SIZE = 16;

    private final AtomicBoolean isTerminated = new AtomicBoolean();
    private final Sinks.Many<Message> messages = Sinks.many().multicast().onBackpressureBuffer();
    private final String entityPath;
    private final String receiveLinkName;
    private final DeliverySettlingMode settlingMode;
    private final boolean includeDeliveryTagInMessage;
    private final  ClientLogger logger;
    private final UnsettledDeliveries unsettledDeliveries;

    /**
     * Creates DeliveryHandler.
     *
     * @param hostName the name of the host hosting the messaging entity identified by {@code entityPath}.
     * @param entityPath the relative path identifying the messaging entity from which the deliveries are
     *                   received from.
     * @param receiveLinkName the name of the amqp receive-link 'Attach'-ed to the messaging entity from
     *                       which the deliveries are received from.
     * @param settlingMode the mode in which DeliveryHandler should operate when settling received deliveries.
     * @param dispatcher the dispatcher to invoke the ProtonJ library APIs.
     * @param timeout the timeout for the {@link DeliveryHandler#sendDisposition(String, DeliveryState)} API.
     * @param retryPolicy the retry configuration for any retriable operation.
     * @param includeDeliveryTagInMessage indicate if the delivery tag should be included in the {@link Message}
     *                                   from {@link DeliveryHandler#getMessages()}'s Flux.
     * @param logger the logger.
     */
    DeliveryHandler(String hostName,
                    String entityPath,
                    String receiveLinkName,
                    DeliverySettlingMode settlingMode,
                    ReactorDispatcher dispatcher,
                    Duration timeout,
                    AmqpRetryPolicy retryPolicy,
                    boolean includeDeliveryTagInMessage,
                    ClientLogger logger) {
        this.entityPath = entityPath;
        this.receiveLinkName = receiveLinkName;
        this.settlingMode = settlingMode;
        this.includeDeliveryTagInMessage = includeDeliveryTagInMessage;
        this.logger = logger;
        this.unsettledDeliveries = new UnsettledDeliveries(hostName, entityPath,
            receiveLinkName, dispatcher,
            timeout, retryPolicy, logger);
    }

    /**
     * Function to notify a {@link Delivery} received from ProtonJ library.
     * <p>
     * The ProtonJ creates {@link Delivery} object upon receiving a transfer or disposition frame from
     * the broker.
     * <p>
     * The transfer frame contains the message, which this function reads from the delivery and streams
     * through the {@link Flux} of {@link Message} returned by {@link DeliveryHandler#getMessages()}.
     * <p>
     * The disposition frame is the broker's ack for the disposition of a delivery that the application
     * requested. The application can request disposition only for deliveries that were earlier received
     * as transfer frames. The ProtonJ library keeps earlier Delivery in-memory objects, and upon receiving
     * the disposition frame, the corresponding delivery object is updated and redelivered to onDelivery.
     * The {@link DeliverySettlingMode#SETTLE_VIA_DISPOSITION} enables this request-ack mode.
     *
     * @param delivery the delivery.
     */
    void onDelivery(Delivery delivery) {
        if (isPartialOrSettledDelivery(delivery) || isDeliverySettledOnClosedLink(delivery)) {
            return;
        }

        final boolean wasSettled = delivery.isSettled();

        switch (settlingMode) {
            case SETTLE_ON_DELIVERY:
                handleSettleOnDelivery(delivery);
                break;
            case ACCEPT_AND_SETTLE_ON_DELIVERY:
                handleAcceptAndSettleOnDelivery(delivery);
                break;
            case SETTLE_VIA_DISPOSITION:
                handleSettleViaDisposition(delivery);
                break;
        }

        final Link link = delivery.getLink();
        if (link != null) {
            final ErrorCondition condition = link.getRemoteCondition();
            addErrorCondition(logger.atVerbose(), condition)
                .addKeyValue(ENTITY_PATH_KEY, entityPath)
                .addKeyValue(LINK_NAME_KEY, receiveLinkName)
                .addKeyValue("updatedLinkCredit", link.getCredit())
                .addKeyValue("remoteCredit", link.getRemoteCredit())
                .addKeyValue("delivery.isPartial", delivery.isPartial())
                .addKeyValue("delivery.isSettled", wasSettled)
                .log("onDelivery.");
        }
    }

    /**
     * Completes the {@link Flux} of {@link Message} returned by {@link DeliveryHandler#getMessages()}.
     *
     * @param errorMessage message to log if completion fails.
     */
    void onDeliveryComplete(String errorMessage) {
        messages.emitComplete((signalType, emitResult) -> {
            logger.atVerbose()
                .addKeyValue(ENTITY_PATH_KEY, entityPath)
                .addKeyValue(LINK_NAME_KEY, receiveLinkName)
                .addKeyValue(EMIT_RESULT_KEY, emitResult)
                .log(errorMessage);
            return false;
        });
    }

    /**
     * Gets the {@link Flux} that streams the {@link Message} objects decoded from the {@link Delivery}
     * received from the broker.
     *
     * @return the {@link Flux} streaming {@link Message}.
     */
    Flux<Message> getMessages() {
        return messages.asFlux();
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
        return unsettledDeliveries.sendDisposition(deliveryTag, desiredState);
    }

    /**
     * Prepare for the closure of this {@link DeliveryHandler}.
     *
     * @return a {@link Mono} that completes upon the completion of close preparation phase.
     */
    Mono<Void> prepareClose() {
        isTerminated.set(true);
        return unsettledDeliveries.prepareClose();
    }

    /**
     * Completes the {@link Flux} of {@link Message} returned by {@link DeliveryHandler#getMessages()},
     * perform any pending work and resource cleanup.
     *
     * @param errorMessage message to log if the {@link Flux} completion fails.
     */
    void finishClose(String errorMessage) {
        onDeliveryComplete(errorMessage);
        unsettledDeliveries.finishClose();
    }

    /**
     * Check if the delivery received from ProtonJ is partial or already settled and log such an event.
     *
     * @param delivery the delivery to inspect.
     * @return {@code true} if the delivery is partial or already settled, {@code false} otherwise.
     */
    private boolean isPartialOrSettledDelivery(Delivery delivery) {
        if (delivery.isPartial()) {
            // A message may span across Transfer frames (e.g., 200kb message over 4 Transfer
            // frames 64k 64k 64k 8k). The ProtonJ Reactor thread will deliver each such Transfer
            // frame as partial Delivery, then a final non-partial Delivery containing the complete
            // message, which means the DeliveryHandler can skip any partial Deliveries.
            final Link link = delivery.getLink();
            if (link != null) {
                final ErrorCondition condition = link.getRemoteCondition();
                addErrorCondition(logger.atVerbose(), condition)
                    .addKeyValue(ENTITY_PATH_KEY, entityPath)
                    .addKeyValue(LINK_NAME_KEY, receiveLinkName)
                    .addKeyValue("updatedLinkCredit", link.getCredit())
                    .addKeyValue("remoteCredit", link.getRemoteCredit())
                    .addKeyValue("delivery.isPartial", true)
                    .addKeyValue("delivery.isSettled", delivery.isSettled())
                    .log("onDelivery.");
            } else {
                logger.atWarning()
                    .addKeyValue(ENTITY_PATH_KEY, entityPath)
                    .addKeyValue("delivery.isSettled", true)
                    .log("Partial delivery with no link.");
            }
            return true;
        }

        if (delivery.isSettled()) {
            // We ran into a case where the ProtonJ Reactor thread delivered duplicate Delivery, which was
            // settled earlier; when handling such a Delivery, ProtonJ hit an IllegalStateException.
            // Until it is fixed in ProtonJ, DeliveryHandler needs to skip such deliveries as a workaround.
            //
            final Link link = delivery.getLink();
            if (link != null) {
                addErrorCondition(logger.atInfo(), link.getRemoteCondition())
                    .addKeyValue(ENTITY_PATH_KEY, entityPath)
                    .addKeyValue(LINK_NAME_KEY, receiveLinkName)
                    .addKeyValue("updatedLinkCredit", link.getCredit())
                    .addKeyValue("remoteCredit", link.getRemoteCredit())
                    .addKeyValue("delivery.isSettled", true)
                    .log("onDelivery. Was already settled.");
            } else {
                logger.atWarning()
                    .addKeyValue(ENTITY_PATH_KEY, entityPath)
                    .addKeyValue("delivery.isSettled", true)
                    .log("Settled delivery with no link.");
            }
            return true;
        }
        return false;
    }

    /**
     * Check if the delivery was received after the closure of receive link; if so, settle the delivery.
     *
     * @param delivery the delivery.
     * @return {@code true} if the delivery was received after the closure of receive link,
     * {@code false} otherwise.
     */
    private boolean isDeliverySettledOnClosedLink(Delivery delivery) {
        final Link link = delivery.getLink();
        if (link != null && link.getLocalState() == EndpointState.CLOSED) {
            // Delivery may get delivered even after the local and remote link states are CLOSED.
            // When the local link is CLOSED, settle and abandon such deliveries. Not settling deliveries
            // in the link will result in `TransportSession` storing all unsettled deliveries in the parent
            // session leading to memory leak when multiple links are opened and closed in the same session.
            delivery.disposition(new Modified());
            delivery.settle();
            return true;
        }
        return false;
    }

    /**
     * Handle the delivery when the settlement mode is {@link DeliverySettlingMode#SETTLE_ON_DELIVERY}.
     *
     * @param delivery the delivery.
     */
    private void handleSettleOnDelivery(Delivery delivery) {
        final Message message;
        try {
            message = decodeTransferDeliveryMessage(delivery, null);
            delivery.settle();
        } catch (RuntimeException decodeError) {
            handleDeliveryDecodeError(decodeError);
            return;
        }
        emitMessage(message, delivery);
    }

    /**
     * Handle the delivery when the settlement mode is {@link DeliverySettlingMode#ACCEPT_AND_SETTLE_ON_DELIVERY}.
     *
     * @param delivery the delivery.
     */
    private void handleAcceptAndSettleOnDelivery(Delivery delivery) {
        final Message message;
        try {
            message = decodeTransferDeliveryMessage(delivery, null);
            delivery.disposition(Accepted.getInstance());
            delivery.settle();
        } catch (RuntimeException decodeError) {
            handleDeliveryDecodeError(decodeError);
            return;
        }
        emitMessage(message, delivery);
    }

    /**
     * Handle the delivery when the settlement mode is {@link DeliverySettlingMode#SETTLE_VIA_DISPOSITION}.
     *
     * @param delivery the delivery.
     */
    private void handleSettleViaDisposition(Delivery delivery) {
        final UUID deliveryTag = decodeDeliveryTag(delivery);
        if (!unsettledDeliveries.containsDelivery(deliveryTag)) {
            final Message message;
            try {
                message = decodeTransferDeliveryMessage(delivery, deliveryTag);
                delivery.getLink().advance();
            } catch (RuntimeException decodeError) {
                handleDeliveryDecodeError(decodeError);
                return;
            }
            if (unsettledDeliveries.onDelivery(deliveryTag, delivery)) {
                emitMessage(message, delivery);
            } else {
                // abandon the delivery as the 'unsettledDeliveries' is being closed.
                delivery.disposition(new Modified());
                delivery.settle();
            }
        } else {
            unsettledDeliveries.onDispositionAck(deliveryTag, delivery);
        }
    }

    /**
     * Read and decode the message from a delivery (delivery that the ProtonJ library created
     * from transfer-frame).
     *
     * @param delivery the delivery
     * @param deliveryTag the unique delivery tag associated with the delivery.
     * @return the decoded message optionally containing the delivery tag.
     */
    private Message decodeTransferDeliveryMessage(Delivery delivery, UUID deliveryTag) {
        final int messageSize = delivery.pending();
        final byte[] buffer = new byte[messageSize];
        final int read = ((Receiver) delivery.getLink()).recv(buffer, 0, messageSize);
        final Message message = Proton.message();
        message.decode(buffer, 0, read);
        if (includeDeliveryTagInMessage) {
            if (deliveryTag == null) {
                return new MessageWithDeliveryTag(message, decodeDeliveryTag(delivery));
            } else {
                return new MessageWithDeliveryTag(message, deliveryTag);
            }
        } else {
            return message;
        }
    }

    /**
     * handles an error upon reading and decoding message from a delivery.
     *
     * @param decodeError the error.
     */
    private void handleDeliveryDecodeError(RuntimeException decodeError) {
        if (decodeError instanceof IllegalStateException && isTerminated.get()) {
            // As part of ReactorReceiver closure, it closes ReceiveLinkHandler and, in turn, DeliverySink.
            // The ReactorReceiver will do its best to schedule that close call, and Receiver.free() from
            // the ProtonJ Reactor thread. If the scheduling fails, that close and Receiver.free() will be
            // called from the ReactorReceiver closure thread; in this case, it is possible to race
            // where the Delivery ProtonJ Reactor thread trying to decode may get released by Receiver.free(),
            // causing IllegalStateException on decode.
            // As this happens in the closure route, the ReactorReceiver.endpointState raise close events
            // Where retries hooks exist, we will not rethrow in this case as it will be propagated to
            // the ProtonJ Reactor thread possibly hosting other healthy Receivers (and Senders).
            emitError(new IllegalStateException("Cannot decode Delivery when ReactorReceiver instance is closed.",
                decodeError));
        } else {
            // Some unknown error :(, notify and rethrow to propagate to ProtonJ Reactor thread.
            emitError(new IllegalStateException("Unexpected error when decoding Delivery.", decodeError));
            throw decodeError;
        }
    }

    /**
     * Emit a message to stream through the {@link Flux} from {@link DeliveryHandler#getMessages()}.
     *
     * @param message the message.
     * @param delivery the delivery from the message read and decoded.
     */
    private void emitMessage(Message message, Delivery delivery) {
        messages.emitNext(message, (signalType, emitResult) -> {
            logger.atWarning()
                .addKeyValue(ENTITY_PATH_KEY, entityPath)
                .addKeyValue(LINK_NAME_KEY, receiveLinkName)
                .addKeyValue(EMIT_RESULT_KEY, emitResult)
                .log("Could not emit delivery. {}", delivery);

            final Link link = delivery.getLink();
            if (emitResult == Sinks.EmitResult.FAIL_OVERFLOW
                && link.getLocalState() != EndpointState.CLOSED) {
                link.setCondition(new ErrorCondition(Symbol.getSymbol("delivery-buffer-overflow"),
                    "Deliveries are not processed fast enough. Closing local link."));
                link.close();
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Terminate the {@link Flux} from {@link DeliveryHandler#getMessages()} by emitting the given error.
     *
     * @param error the error.
     */
    private void emitError(IllegalStateException error) {
        messages.emitError(error, (signalType, emitResult) -> {
            logger.atVerbose()
                .addKeyValue(ENTITY_PATH_KEY, entityPath)
                .addKeyValue(LINK_NAME_KEY, receiveLinkName)
                .addKeyValue(EMIT_RESULT_KEY, emitResult)
                .log("Could not emit messages.error.", error);
            return false;
        });
    }

    private static UUID decodeDeliveryTag(Delivery delivery) {
        final byte[] deliveryTag = delivery.getTag();
        if (deliveryTag == null || deliveryTag.length != DELIVERY_TAG_SIZE) {
            // Per standard AMQP contract, the Delivery Tag is allowed to be up to 32 bytes.
            // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-delivery-tag
            // The Azure AMQP Service(s) contract is, if Delivery Tag present, it will be
            // strictly a GUID (16 bytes), safely treating any nonconforming Delivery Tag
            // as if it does not present.
            return DELIVERY_EMPTY_TAG;
        }

        // Translate byte encoded GUID to UUID.
        final byte[] reorderedBytes = new byte[DELIVERY_TAG_SIZE];
        for (int i = 0; i < DELIVERY_TAG_SIZE; i++) {
            int indexInReorderedBytes;
            switch (i) {
                case 0:
                    indexInReorderedBytes = 3;
                    break;
                case 1:
                    indexInReorderedBytes = 2;
                    break;
                case 2:
                    indexInReorderedBytes = 1;
                    break;
                case 3:
                    indexInReorderedBytes = 0;
                    break;
                case 4:
                    indexInReorderedBytes = 5;
                    break;
                case 5:
                    indexInReorderedBytes = 4;
                    break;
                case 6:
                    indexInReorderedBytes = 7;
                    break;
                case 7:
                    indexInReorderedBytes = 6;
                    break;
                default:
                    indexInReorderedBytes = i;
            }
            reorderedBytes[indexInReorderedBytes] = deliveryTag[i];
        }
        final ByteBuffer buffer = ByteBuffer.wrap(reorderedBytes);
        final long mostSignificantBits = buffer.getLong();
        final long leastSignificantBits = buffer.getLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
