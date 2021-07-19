package com.contoso.receiver;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.PartitionReceiver;
import com.microsoft.azure.eventhubs.ReceiverOptions;
import com.microsoft.azure.eventhubs.TransportType;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Track1_ReceiveFromPartition {
    private CompletableFuture<EventHubClient> clientFuture;
    private final String partitionId;
    private final int preFetch;
    private final int count;
    private final String consumerGroup;
    private final ScheduledExecutorService scheduler;
    private final int receiverCount = 1;

    public Track1_ReceiveFromPartition(String connectionString,
                                       String eventHubName,
                                       String consumerGroup,
                                       String partitionId,
                                       int preFetch,
                                       int count) {
        this.consumerGroup = consumerGroup;
        this.partitionId = partitionId;
        this.preFetch = preFetch;
        this.count = count;
        this.scheduler = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors() * 4);

        final ConnectionStringBuilder builder = new ConnectionStringBuilder(connectionString)
            .setEventHubName(eventHubName);

        builder.setTransportType(TransportType.AMQP);

        try {
            this.clientFuture = EventHubClient.createFromConnectionString(builder.toString(), scheduler);
        } catch (IOException e) {
            this.clientFuture = new CompletableFuture<>();
            this.clientFuture.completeExceptionally(new UncheckedIOException("Unable to create EventHubClient.", e));
        }
    }

    public void run() {
        final CompletableFuture<PartitionReceiver> partitionReceiver = this.clientFuture.thenComposeAsync(client -> {
            try {
                final ReceiverOptions receiverOptions = new ReceiverOptions();
                receiverOptions.setPrefetchCount(this.preFetch);

                return client.createReceiver(this.consumerGroup,
                    this.partitionId, com.microsoft.azure.eventhubs.EventPosition.fromStartOfStream(), receiverOptions);
            } catch (EventHubException e) {
                final CompletableFuture<PartitionReceiver> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("Unable to create PartitionReceiver", e));
                return future;
            }
        });

        long startNanoTime = System.nanoTime();
        final EventsHandler handler = new EventsHandler(this.count, this.preFetch);
        final CompletableFuture<Void> receiveOperation = partitionReceiver
            .thenCompose(receiver -> receiver.setReceiveHandler(handler))
            .thenCompose(unused -> handler.isCompleteReceiving());

        Mono.fromCompletionStage(receiveOperation
            .thenCompose(unused -> partitionReceiver)
            .thenCompose(PartitionReceiver::close)).block();

        long endNanoTime = System.nanoTime();
        System.out.println("Took " + Duration.ofNanos(endNanoTime - startNanoTime).getSeconds()
            + " seconds to receive "
            + handler.eventsReceived() + " events");
        System.out.println("Closing-Client");
        Mono.fromCompletionStage(clientFuture.thenCompose(EventHubClient::close)).block();
        scheduler.shutdown();
        System.out.println("Closed-Client");
    }
}
