// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

import com.azure.core.util.logging.ClientLogger;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The type tracks the downstream request accumulated since the last broker flow and sends a flow
 * once the value is greater than or equal to the Prefetch.
 */
final class RequestDrivenCreditAccounting extends CreditAccounting {
    private static final int MAX_VALUE_BOUND = 100;
    private long pendingMessageCount;
    private final AtomicLong requestAccumulated = new AtomicLong(0);

    /**
     * Create new CreditAccounting to track the downstream request accumulated and use it to compute
     * the credit to send.
     *
     * @param receiver the receiver for sending credit to the broker.
     * @param subscription the subscription to the receiver's message publisher to request messages when
     *                    needed (the publisher won't translate these requests to network flow performative).
     * @param prefetch the prefetch configured.
     * @param logger the logger.
     */
    RequestDrivenCreditAccounting(ReactorReceiver receiver, Subscription subscription, int prefetch, ClientLogger logger) {
        super(receiver, subscription, validateAndBound(prefetch), logger);
    }

    @Override
    void update(long request, long emitted) {
        // Non-thread-safe method, designed ONLY to be called from the serialized drain-loop of message-flux.
        pendingMessageCount -= emitted;
        final long c = request - pendingMessageCount + prefetch;
        if (c > 0) {
            pendingMessageCount += c;
            subscription.request(c);
            if (requestAccumulated.addAndGet(c) >= prefetch) {
                scheduleFlow(() -> requestAccumulated.getAndSet(0));
            }
        }
    }

    private static int validateAndBound(int prefetch) {
        if (prefetch < 0) {
            throw new IllegalArgumentException("prefetch >= 0 required but it was " + prefetch);
        }
        return prefetch == Integer.MAX_VALUE ? MAX_VALUE_BOUND : prefetch;
    }
}
