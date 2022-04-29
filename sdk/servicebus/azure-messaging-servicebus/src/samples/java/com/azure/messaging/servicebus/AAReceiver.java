package com.azure.messaging.servicebus;

import com.azure.core.amqp.AmqpEndpointState;
import com.azure.core.amqp.exception.AmqpErrorCondition;
import com.azure.core.amqp.exception.AmqpException;
import org.apache.qpid.proton.engine.EndpointState;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.azure.core.amqp.implementation.ClientConstants.ENTITY_PATH_KEY;

public class AAReceiver {
    static String connectionString = System.getenv("CON_STR");
    static String queueName = System.getenv("Q_NAME");

    public static void main(String[] args) throws InterruptedException {

        while (true) {
            CountDownLatch latch = new CountDownLatch(1);

            ServiceBusReceiverAsyncClient receiver = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .maxAutoLockRenewDuration(Duration.ZERO)
                .queueName(queueName)
                .buildAsyncClient();

            Disposable subscription = receiver.receiveMessages()
                .subscribe(
                    (message) -> {
                        // receiver.complete(message).subscribe();
                        System.out.println("Message# " + message);
                    },
                    error -> {
                        System.out.println("errored");
                        error.printStackTrace();
                        latch.countDown();
                    },
                    () -> {
                        System.out.println("completed");
                        latch.countDown();
                    }
                );

            latch.await();

            subscription.dispose();

            System.out.println("recurse....");
        }

        // TimeUnit.MINUTES.sleep(100);

    }

    public static void foo() {
        CountDownLatch latch = new CountDownLatch(1);
        final Sinks.Many<EndpointState> endpointStates = Sinks.many().replay()
            .latestOrDefault(EndpointState.UNINITIALIZED);

        Schedulers.boundedElastic().schedule(() -> {
            endpointStates.tryEmitComplete();
            // endpointStates.tryEmitComplete();
        }, 2, TimeUnit.SECONDS);

        endpointStates.asFlux().distinctUntilChanged()
            .filter(e -> e == EndpointState.ACTIVE)
            .next()
            .timeout(Duration.ofSeconds(10))
            .subscribe(
                s -> {
                    System.out.println("subscribe.onNext:" + s);
                },
                e -> {
                    System.out.println("subscribe.onError");
                },
                () -> {
                    System.out.println("subscribe.onComplete");
                });



//        ServiceBusReceiverAsyncClient receiver = new ServiceBusClientBuilder()
//            .connectionString(connectionString)
//            .receiver()
//            .maxAutoLockRenewDuration(Duration.ZERO)
//            .disableAutoComplete()
//            .queueName(queueName)
//            .buildAsyncClient();
//
//        Disposable subscription = receiver.receiveMessages()
//            .subscribe(
//                (message) ->  {
//                    System.out.println("Message# " + message);
//                },
//                error -> {
//                    System.out.println("errored");
//                    error.printStackTrace();
//                    latch.countDown();
//                },
//                () -> {
//                    System.out.println("completed");
//                    latch.countDown();
//                }
//            );
//
//        latch.await(20, TimeUnit.MINUTES);

//        subscription.dispose();



//        TimeUnit.SECONDS.sleep(6);
//
//        endpointStates.asFlux().distinctUntilChanged()
//            .takeUntil(e -> e == EndpointState.ACTIVE)
//            .timeout(Duration.ofSeconds(10))
//            .then(Mono.just(1))
//            .subscribe(s -> {
//                    System.out.println("subscribe.onNext:" + s);
//                },
//                e -> {
//                    System.out.println("subscribe.onError");
//                },
//                () -> {
//                    System.out.println("subscribe.onComplete");
//                });



//        Flux.defer(() -> Flux.range(1, 100))
//            .delayElements(Duration.ofSeconds(1))
//            .map(v -> {
//                    if (v % 2 == 0) {
//                        throw new RuntimeException("OK_RETRY");
//                    } else if (v % 11 == 0) {
//                        throw new RuntimeException("DONT_RETRY");
//                    }
//                    return v;
//                })
//            .retryWhen(Retry.from(retrySignals -> retrySignals.flatMap(signal -> {
//                RuntimeException re = (RuntimeException) signal.failure();
//                if (re.getMessage().equalsIgnoreCase("OK_RETRY")) {
//                    System.out.println("retrying");
//                    return Mono.delay(Duration.ofSeconds(2));
//                } else {
//                    return Mono.<Long>error(re);
//                }
//            })))
//            .retryWhen(Retry.from(companion -> companion.handle((retrySignal, sink) -> {
//                RuntimeException re = (RuntimeException) retrySignal.failure();
//                if (re.getMessage().equalsIgnoreCase("OK_RETRY")) {
//                    sink.complete();
//                } else {
//                    System.out.println("not retrying...");
//                    sink.error(re);
//                }
//                })))
//                .subscribe(v -> {
//                    System.out.println(v);
//                },
//                e -> {
//                System.out.println("subscription_completed_error");
//                },
//                () -> {
//                    System.out.println("subscription_completed");
//                });
    }
}
