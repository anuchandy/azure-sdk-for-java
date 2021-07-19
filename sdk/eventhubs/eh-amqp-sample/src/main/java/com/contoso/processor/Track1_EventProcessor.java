package com.contoso.processor;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.TransportType;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.InMemoryCheckpointManager;
import com.microsoft.azure.eventprocessorhost.InMemoryLeaseManager;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Track1_EventProcessor {
    private static final String STORAGE_PREFIX = "perf";
    private final String connectionString;
    private final String eventHubName;
    private final String consumerGroup;
    private final String storageUrl;
    private final String storageConnectionString;
    private final boolean inMemoryState;
    private final SampleEventProcessorFactory processorFactory;
    private final ConcurrentHashMap<String, SamplePartitionProcessorT1> partitionProcessorMap;
    private final ScheduledExecutorService scheduler;
    //
    private StorageCredentials storageCredentials;
    private String containerName;
    private CloudBlobContainer containerReference;

    public Track1_EventProcessor(String connectionString,
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
        this.processorFactory = new SampleEventProcessorFactory(partitionProcessorMap);
        this.scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 4);

        if (this.inMemoryState) {
            this.storageCredentials = null;
            this.containerName = null;
            this.containerReference = null;
        } else {
            try {
                this.storageCredentials = StorageCredentials.tryParseCredentials(this.storageConnectionString);
                this.containerName = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHMMss"));
                final StorageUri storageUri = new StorageUri(URI.create(this.storageUrl));
                final CloudBlobClient client = new CloudBlobClient(storageUri, this.storageCredentials);
                this.containerReference = client.getContainerReference(this.containerName);

                if (this.containerReference.deleteIfExists()) {
                    System.out.printf("Deleting %s because it existed before.%n", this.containerName);
                }
                this.containerReference.create();
            } catch (InvalidKeyException | StorageException e) {
                throw new RuntimeException("Unable to parse credentials or container name.", e);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Unable to create container: " + this.containerName, e);
            }
        }
    }

    public void run(RunInfoCallback callback) {
        final ConnectionStringBuilder connBuilder = new ConnectionStringBuilder(this.connectionString)
            .setEventHubName(this.eventHubName);
        connBuilder.setTransportType(TransportType.AMQP);

        // 1. Initialize the partitionProcessorMap
        Mono.usingWhen(
            Mono.fromCompletionStage(() -> {
                CompletableFuture<EventHubClient> ehClient;
                try {
                    ehClient = EventHubClient.createFromConnectionString(connBuilder.toString(), scheduler);
                } catch (IOException e) {
                    ehClient = new CompletableFuture<>();
                    ehClient.completeExceptionally(new UncheckedIOException("Unable to create EventHubClient.", e));
                }
                return ehClient;
            }),
            ehClient -> Mono.fromCompletionStage(ehClient.getRuntimeInformation())
                .doOnNext(runtimeInformation -> {
                    for (String id : runtimeInformation.getPartitionIds()) {
                        partitionProcessorMap.put(id, new SamplePartitionProcessorT1());
                    }
                })
                .then(),
            ehClient -> Mono.fromCompletionStage(ehClient.close()))
            .block();

        // 2. Initialize the Processor
        final Mono<EventProcessorHost> createProcessor;
        if (this.inMemoryState) {
            createProcessor = Mono.fromCallable(() -> {
                InMemoryCheckpointManager inMemoryCheckpointManager = new InMemoryCheckpointManager();
                InMemoryLeaseManager inMemoryLeaseManager = new InMemoryLeaseManager();
                final EventProcessorHost.EventProcessorHostBuilder.OptionalStep builder =
                    EventProcessorHost.EventProcessorHostBuilder.newBuilder(
                        connBuilder.getEndpoint().toString(), this.consumerGroup)
                        .useUserCheckpointAndLeaseManagers(inMemoryCheckpointManager, inMemoryLeaseManager)
                        .useEventHubConnectionString(connBuilder.toString())
                        .setExecutor(this.scheduler);
                return builder.build();
            });
        } else {
            createProcessor = Mono.fromCallable(() -> {
                final EventProcessorHost.EventProcessorHostBuilder.OptionalStep builder =
                    EventProcessorHost.EventProcessorHostBuilder.newBuilder(
                        connBuilder.getEndpoint().toString(), this.consumerGroup)
                        .useAzureStorageCheckpointLeaseManager(this.storageCredentials, this.containerName, STORAGE_PREFIX)
                        .useEventHubConnectionString(connBuilder.toString())
                        .setExecutor(this.scheduler);
                return builder.build();
            });
        }

        // 3. Run processor
        final long[] startTime = new long[1];
        final long[] endTime = new long[1];

        Mono.usingWhen(
            createProcessor,
            processor -> {
                startTime[0] = System.nanoTime();
                // System.out.println("Starting run:T1.");
                return Mono.fromCompletionStage(
                    processor.registerEventProcessorFactory(processorFactory))
                    .then(Mono.when(Mono.delay(Duration.ofSeconds(120))));
            },
            processor -> {
                endTime[0] = System.nanoTime();
                // System.out.println("Completed run:T1.");
                return Mono.fromCompletionStage(processor.unregisterEventProcessor());
            })
            .doFinally(signal -> {
                // System.out.println("Finished cleaning up processor resources.");
            })
            .block();
        outputPartitionResults(startTime[0], endTime[0], callback);
    }

    private void outputPartitionResults(long startTime, long endTime, RunInfoCallback callback) {
        long total = 0;
        List<String> partitionStats = new ArrayList<>();
        for (SamplePartitionProcessorT1 processor : partitionProcessorMap.values()) {
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

        callback.call("T1", total, elapsedTime, eventsPerSecond, partitionStats);
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
