package com.contoso.processor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Keeps track of events received.
 */
public class EventsCounter {
    private final String partitionId;
    private final AtomicInteger eventsReceived = new AtomicInteger();
    private volatile Long startTime;
    private volatile Long stopTime;

    /**
     * Creates a new instance.
     *
     * @param partitionId The partition id for this counter.
     */
    EventsCounter(String partitionId) {
        this.partitionId = partitionId;
    }

    /**
     * Gets the partition id.
     *
     * @return The partition id.
     */
    String getPartitionId() {
        return partitionId;
    }

    /**
     * Increment number of events.
     */
    void increment() {
        eventsReceived.incrementAndGet();
    }

    /**
     * Gets the number of events.
     *
     * @return The number of events.
     */
    int totalEvents() {
        return eventsReceived.get();
    }

    /**
     * Elapsed time in nanoseconds
     *
     * @return Elapsed time in nanoseconds.
     */
    long elapsedTime() {
        if (stopTime == null || startTime == null) {
            return -1L;
        }
        return stopTime - startTime;
    }

    /**
     * Starts the counter time.
     */
    synchronized void start() {
        if (startTime != null) {
            return;
        }

        startTime = System.nanoTime();
    }

    /**
     * Ends the counter time.
     */
    synchronized void stop() {
        if (stopTime != null) {
            return;
        }

        stopTime = System.nanoTime();
    }
}
