package com.contoso.receiver;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerAsyncClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Track2_ReceiveFromPartition {
    private final EventHubConsumerAsyncClient receiver;
    private final String partitionId;
    private final int count;
    private final int receiverCount = 1;

    public Track2_ReceiveFromPartition(String connectionString,
                                       String eventHubName,
                                       String consumerGroup,
                                       String partitionId,
                                       int preFetch,
                                       int count) {
        this.partitionId = partitionId;
        this.count = count;
        this.receiver = new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .transportType(AmqpTransportType.AMQP)
            .prefetchCount(preFetch)
            .consumerGroup(consumerGroup)
            .buildAsyncConsumerClient();
    }
    public void run() {
        List<int[]> eventsCountList = new ArrayList<>(receiverCount);
        for (int i = 0; i< receiverCount; i++) {
            eventsCountList.add(new int[1]);
        }

        long startNanoTime = System.nanoTime();
        Flux.fromIterable(eventsCountList)
            .flatMap(events -> {
                return receiver.receiveFromPartition(partitionId, EventPosition.earliest())
                    .take(count, true)
                    .doOnNext(e -> events[0]++)
                    .then();
            }, 1)
            .blockLast();

        long endNanoTime = System.nanoTime();

        int sumOfEvents = eventsCountList.stream().collect(Collectors.summingInt(e -> e[0]));

        System.out.println("Took " + Duration.ofNanos(endNanoTime - startNanoTime).getSeconds() + " seconds to receive " + sumOfEvents + " events");
        System.out.println("Closing-Client");
        receiver.close();
        System.out.println("Closed-Client");
    }
}
