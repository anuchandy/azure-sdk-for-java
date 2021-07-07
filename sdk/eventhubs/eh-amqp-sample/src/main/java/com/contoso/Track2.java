package com.contoso;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerAsyncClient;
import com.azure.messaging.eventhubs.models.EventPosition;

import java.time.Duration;

public class Track2 {
    public static void run(String connectionString,
                           String eventHubName,
                           String consumerGroup,
                           String partitionId,
                           int preFetch,
                           int count) {
        final EventHubConsumerAsyncClient receiver = new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .transportType(AmqpTransportType.AMQP)
            .prefetchCount(preFetch)
            .consumerGroup(consumerGroup)
            .buildAsyncConsumerClient();

        long startNanoTime = System.nanoTime();
        int [] events = new int[1];
        receiver.receiveFromPartition(partitionId, EventPosition.earliest())
            .take(count)
            .doOnNext(e -> events[0]++)
            .blockLast();
        long endNanoTime = System.nanoTime();
        System.out.println("Took " + Duration.ofNanos(endNanoTime - startNanoTime).getSeconds() + " seconds to receive " + events[0] + " events");
        System.out.println("Closing-Client");
        receiver.close();
        System.out.println("Closed-Client");
    }
}
