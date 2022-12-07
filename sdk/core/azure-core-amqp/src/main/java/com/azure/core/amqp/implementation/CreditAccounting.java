// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

import org.reactivestreams.Subscription;

import java.util.function.Supplier;

/**
 * The type tracking the credits for the receiver in message-flux. It decides when to request messages
 * to the receiver's message publisher and send the credits to the broker.
 */
abstract class CreditAccounting {
    private final ReactorReceiver receiver;
    protected final Subscription subscription;
    protected final int prefetch;

    /**
     * Create new CreditAccounting to track credit associated with a receiver backing a mediator.
     *
     * @param receiver the receiver for sending credit to the broker.
     * @param subscription the subscription to the receiver's message publisher to request messages when
     *                    needed (the publisher won't translate these requests to network flow performative).
     * @param prefetch the prefetch configured.
     */
    protected CreditAccounting(ReactorReceiver receiver, Subscription subscription, int prefetch) {
        this.receiver = receiver;
        this.subscription = subscription;
        this.prefetch = prefetch;
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
    abstract void update(long request, long emitted);

    /**
     * Request receiver to schedule sending of a flow performative to the broker.
     *
     * @param creditSupplier the supplier that supplies the credit to send using flow.
     */
    protected void scheduleFlow(Supplier<Long> creditSupplier) {
        // TODO (anu): Try-Catch-Log
        receiver.scheduleFlow(creditSupplier);
    }
}
