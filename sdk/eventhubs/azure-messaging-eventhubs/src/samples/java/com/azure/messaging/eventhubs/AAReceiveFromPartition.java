package com.azure.messaging.eventhubs;

import com.azure.core.amqp.AmqpRetryMode;
import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.amqp.implementation.handler.EndpointsReferences;
import com.azure.core.util.logging.ClientLogger;
import com.azure.messaging.eventhubs.implementation.EventHubReactorAmqpConnection;
import com.azure.messaging.eventhubs.models.EventPosition;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class AAReceiveFromPartition {
    static ClientLogger LOGGER = new ClientLogger(AAReceiveFromPartition.class);

    public static void main(String[] args) throws InterruptedException {

        final Map<String, EventPosition> eventPositions = new HashMap<>();

        AmqpRetryOptions amqpRetryOptions = new AmqpRetryOptions();
        amqpRetryOptions.setMode(AmqpRetryMode.FIXED);
        amqpRetryOptions.setMaxDelay(Duration.ofSeconds(15));
        amqpRetryOptions.setMaxRetries(25);


        EventHubConsumerAsyncClient consumerClient = new EventHubClientBuilder()
            .retryOptions(amqpRetryOptions)
            .connectionString(System.getenv("CON_STR"), "eh4")
            .consumerGroup("$Default")
            .buildAsyncConsumerClient();

        consumerClient.getPartitionIds()
            .doOnNext(partitionId -> {
                    eventPositions.put(partitionId, EventPosition.latest());
             }).blockLast();

//        eventPositions.put("0", EventPosition.latest());
//        eventPositions.put("1", EventPosition.latest());
//        eventPositions.put("2", EventPosition.latest());
//        eventPositions.put("3", EventPosition.latest());

        CountDownLatch isFinished = new CountDownLatch(eventPositions.size() - 1);
        CountDownLatch isError = new CountDownLatch(1);
        AtomicReference<Throwable> errorException = new AtomicReference<>();

        Schedulers.boundedElastic().schedule(() -> {
            EndpointsReferences.print();
            EventHubReactorAmqpConnection connection = consumerClient.getCurrentConnection();
            EndpointsReferences.closeInOrder(connection.getReactorProvider().getReactorDispatcher());
        }, 10, TimeUnit.SECONDS);

        for(Map.Entry<String, EventPosition> entry : eventPositions.entrySet()) {
            LOGGER.info("Starting EventHub Consumer for partitionId '{}'.", entry.getKey());
            consumerClient
                .receiveFromPartition(entry.getKey(), entry.getValue())
                .subscribe(
                    event -> {
                        System.out.println("received");
                    },
                    throwable -> {
                        LOGGER.error("EventHub Consumer for partitionId '{}' failed with exception: '{}'; error: '{}'; stacktrace: '{}'",
                            entry.getKey(),
                            throwable.getClass().getName(), throwable.getMessage(), throwable.getStackTrace());
                        errorException.set(throwable);
                        isError.countDown();
                        isFinished.countDown();
                    },
                    () -> {
                        LOGGER.info("EventHub Consumer for partitionId '{}' stopped successfully.", entry.getKey());
                        isFinished.countDown();
                    });

        }

        // wait until a single error occurs
        isError.await();

        TimeUnit.SECONDS.sleep(5);
        // throw the error to exit the application
        if (errorException.get() != null) {
            errorException.get().printStackTrace();
        }
    }
}
