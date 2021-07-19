package com.contoso.processor;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.LoadBalancingStrategy;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Track2_EventProcessor {
    private final String connectionString;
    private final String eventHubName;
    private final String consumerGroup;
    private final String storageUrl;
    private final String storageConnectionString;
    private final boolean inMemoryState;
    private BlobContainerAsyncClient containerClient;
    private final ConcurrentHashMap<String, SamplePartitionProcessorT2> partitionProcessorMap;

    public Track2_EventProcessor(String connectionString,
                                 String eventHubName,
                                 String consumerGroup,
                                 int preFetch,
                                 int count,
                                 String storageUrl,
                                 String storageConnectionString,
                                 boolean inMemoryState) {
        this.connectionString = connectionString;
        this.eventHubName = eventHubName;
        this.consumerGroup = consumerGroup;
        this.storageUrl = storageUrl;
        this.storageConnectionString = storageConnectionString;
        this.inMemoryState = inMemoryState;
        this.partitionProcessorMap = new ConcurrentHashMap<>();
        if (this.inMemoryState) {
            this.containerClient = null;
        } else {
            final String containerName = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss"));
            this.containerClient = new BlobContainerClientBuilder()
                .connectionString(this.storageConnectionString)
                .containerName(containerName)
                .buildAsyncClient();

            final Mono<Void> createContainerMono = containerClient.exists().flatMap(exists -> {
                if (exists) {
                    return containerClient.delete().then(containerClient.create());
                } else {
                    return containerClient.create();
                }
            });
            createContainerMono.block();
        }
    }

    public void run(RunInfoCallback callback) {
        Map<String, EventPosition> initialPositions = new HashMap<>();

        // 1. Initialize the partitionProcessorMap
        final EventHubClientBuilder ehBuilder = new EventHubClientBuilder()
            .connectionString(this.connectionString, this.eventHubName)
            .transportType(AmqpTransportType.AMQP);
        final EventHubProducerAsyncClient ehClient = ehBuilder.buildAsyncProducerClient();
        ehClient.getEventHubProperties()
            .doOnNext(properties -> {
                for (String partitionId : properties.getPartitionIds()) {
                    this.partitionProcessorMap.put(partitionId, new SamplePartitionProcessorT2());
                    initialPositions.put(partitionId, EventPosition.earliest());
                }
            }).block();

        // 2. Initialize the Processor
        EventProcessorClientBuilder processorBuilder = new EventProcessorClientBuilder()
            .connectionString(this.connectionString, this.eventHubName)
            .consumerGroup(this.consumerGroup)
            .loadBalancingStrategy(LoadBalancingStrategy.GREEDY)
            .initialPartitionEventPosition(initialPositions);
        if (this.inMemoryState) {
            processorBuilder.checkpointStore(new SampleCheckpointStore());
        } else  {
            final BlobCheckpointStore checkpointStore = new BlobCheckpointStore(containerClient);
            processorBuilder.checkpointStore(checkpointStore);
        }
        processorBuilder.processError(context -> {
                final String partitionId = context.getPartitionContext().getPartitionId();
                final SamplePartitionProcessorT2 processor = partitionProcessorMap.get(partitionId);
                if (processor == null) {
                    System.err.printf("partitionId: %s. No matching processor. onError: %s%n",
                        partitionId, context.getThrowable());
                } else {
                    processor.onError(context.getPartitionContext(), context.getThrowable());
                }
            })
            .processPartitionInitialization(context -> {
                final String partitionId = context.getPartitionContext().getPartitionId();
                final SamplePartitionProcessorT2 processor = partitionProcessorMap.get(partitionId);
                if (processor == null) {
                    System.err.printf("partitionId: %s. No matching processor. onOpen%n", partitionId);
                } else {
                    processor.onOpen(context.getPartitionContext());
                }
            })
            .processPartitionClose(context -> {
                final String partitionId = context.getPartitionContext().getPartitionId();
                final SamplePartitionProcessorT2 processor = partitionProcessorMap.get(partitionId);
                if (processor == null) {
                    System.err.printf("partitionId: %s. No matching processor. onClose%n", partitionId);
                } else {
                    processor.onClose(context.getPartitionContext(), context.getCloseReason());
                }
            })
            .processEvent(context -> {
                final String partitionId = context.getPartitionContext().getPartitionId();
                final SamplePartitionProcessorT2 processor = partitionProcessorMap.get(partitionId);
                if (processor == null) {
                    System.err.printf("partitionId: %s. No matching processor. onEvent%n", partitionId);
                } else {
                    processor.onEvents(context.getPartitionContext(), context.getEventData());
                }
            });
            processorBuilder.transportType(AmqpTransportType.AMQP);
            final EventProcessorClient processorClient = processorBuilder.buildEventProcessorClient();

        // 3. Run processor
        final long[] startTime = new long[1];
        final long[] endTime = new long[1];
        Mono.usingWhen(Mono.just(processorClient),
                processor -> {
                    startTime[0] = System.nanoTime();
                    // System.out.println("Starting run:T2");
                    processor.start();
                    return Mono.delay(Duration.ofSeconds(120)).then();
                },
                processor-> {
                    endTime[0] = System.nanoTime();
                    // System.out.println("Completed run:T2");
                    return Mono.delay(Duration.ofMillis(500), Schedulers.boundedElastic())
                        .then(Mono.fromRunnable(() -> processor.stop()));
                })
                .block();

        outputPartitionResults(startTime[0], endTime[0], callback);
    }

    private void outputPartitionResults(long startTime, long endTime, RunInfoCallback callback) {
        long total = 0;
        List<String> partitionStats = new ArrayList<>();
        for (SamplePartitionProcessorT2 processor : partitionProcessorMap.values()) {
            processor.onStop();
            final List<EventsCounter> counters = processor.getCounters();
            for (int i = 0; i < counters.size(); i++) {
                final EventsCounter eventsCounter = counters.get(i);
                total += eventsCounter.totalEvents();
                final String result = getResults(i, eventsCounter);
                partitionStats.add(result);
            }
        }
        double elapsedTime = (endTime - startTime) * 0.000000001;
        double eventsPerSecond = total / elapsedTime;

        callback.call("T2", total, elapsedTime, eventsPerSecond, partitionStats);
//        System.out.println("");
//        System.out.println(String.format("Total Events\t%d%n", total));
//        System.out.println(String.format("Total Duration (s)\t%.2f%n", elapsedTime));
//        System.out.println(String.format("Rate (events/s)\t%.2f%n", eventsPerSecond));
    }

    private String getResults(int index, EventsCounter eventsCounter) {
        final double seconds = eventsCounter.elapsedTime() * 0.000000001;
        final double operationsSecond = eventsCounter.totalEvents() / seconds;
        return String.format("%s\t%d\t%d\t%s\t%s\t%.2f", eventsCounter.getPartitionId(), index,
            eventsCounter.totalEvents(), eventsCounter.elapsedTime(), seconds, operationsSecond);
    }
}
