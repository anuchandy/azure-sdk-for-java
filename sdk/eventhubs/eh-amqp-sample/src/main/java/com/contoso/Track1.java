package com.contoso;

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

public class Track1 {
    public static void run(String connectionString,
                           String eventHubName,
                           String consumerGroup,
                           String partitionId,
                           int preFetch,
                           int count) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors() * 4);

        final ConnectionStringBuilder builder = new ConnectionStringBuilder(connectionString)
            .setEventHubName(eventHubName);

        builder.setTransportType(TransportType.AMQP);

        CompletableFuture<EventHubClient> clientFuture;
        try {
            clientFuture = EventHubClient.createFromConnectionString(builder.toString(), scheduler);
        } catch (IOException e) {
            clientFuture = new CompletableFuture<>();
            clientFuture.completeExceptionally(new UncheckedIOException("Unable to create EventHubClient.", e));
        }

        final EventsHandler handler = new EventsHandler(count, preFetch);
        final CompletableFuture<PartitionReceiver> partitionReceiver = clientFuture.thenComposeAsync(client -> {
            try {
                final ReceiverOptions receiverOptions = new ReceiverOptions();
                receiverOptions.setPrefetchCount(preFetch);

                return client.createReceiver(consumerGroup,
                    partitionId, com.microsoft.azure.eventhubs.EventPosition.fromStartOfStream(), receiverOptions);
            } catch (EventHubException e) {
                final CompletableFuture<PartitionReceiver> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("Unable to create PartitionReceiver", e));
                return future;
            }
        });

        long startNanoTime = System.nanoTime();
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
