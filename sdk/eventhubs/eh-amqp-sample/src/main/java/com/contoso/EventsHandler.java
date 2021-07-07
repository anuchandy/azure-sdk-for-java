package com.contoso;

import com.microsoft.azure.eventhubs.PartitionReceiveHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

final class EventsHandler implements PartitionReceiveHandler {
    private final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
    private final AtomicLong numberOfEvents;
    private final int prefetch;
    private final long requestedEvents;

    EventsHandler(int numberOfEvents, int prefetch) {
        this.requestedEvents = numberOfEvents;
        this.numberOfEvents = new AtomicLong(numberOfEvents);
        this.prefetch = prefetch;
    }

    @Override
    public int getMaxEventCount() {
        return prefetch;
    }

    @Override
    public void onReceive(Iterable<com.microsoft.azure.eventhubs.EventData> events) {
        if (events == null) {
            return;
        }

        final long count = StreamSupport.stream(events.spliterator(), false).count();
        final long left = numberOfEvents.addAndGet(-count);
        if (left <= 0) {
            completableFuture.complete(null);
        }
    }

    @Override
    public void onError(Throwable error) {
        completableFuture.completeExceptionally(error);
    }

    /**
     * Completes when the total number of events are received.
     *
     * @return A future that completes when all items are received.
     */
    public CompletableFuture<Void> isCompleteReceiving() {
        return completableFuture;
    }

    public long eventsReceived() {
        return this.requestedEvents - this.numberOfEvents.get();
    }
}
