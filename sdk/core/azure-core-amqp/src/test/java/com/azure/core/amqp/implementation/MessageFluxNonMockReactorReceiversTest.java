// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

import com.azure.core.amqp.AmqpConnection;
import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.amqp.AmqpRetryPolicy;
import com.azure.core.amqp.FixedAmqpRetryPolicy;
import com.azure.core.amqp.implementation.handler.DeliverySettleMode;
import com.azure.core.amqp.implementation.handler.ReceiveLinkHandler;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.messaging.Source;
import org.apache.qpid.proton.amqp.transport.ErrorCondition;
import org.apache.qpid.proton.engine.Delivery;
import org.apache.qpid.proton.engine.EndpointState;
import org.apache.qpid.proton.engine.Event;
import org.apache.qpid.proton.engine.Receiver;
import org.apache.qpid.proton.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.azure.core.amqp.implementation.AmqpErrorCode.LINK_DETACH_FORCED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageFluxNonMockReactorReceiversTest {
    private static final String BROKER_FQDN = "contoso.servicebus.windows.net";
    private static final String ENTITY_PATH = "orders";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy.HH:mm:ss");
    private final AmqpRetryOptions retryOptions = new AmqpRetryOptions().setMaxRetries(3).setDelay(Duration.ofMillis(2));
    private final AmqpRetryPolicy retryPolicy = new FixedAmqpRetryPolicy(retryOptions);
    private final AmqpMetricsProvider metricsProvider = new AmqpMetricsProvider(null, BROKER_FQDN, ENTITY_PATH);

    @Test
    public void messageConcurrency() {
        final int prefetch = 0;
        final int concurrency = 4;

        final Deque<RemoteLinkSettings> linkSettings = new ArrayDeque<>();
        // RemoteLinkSettings Ctr takes (String linkId, Duration detachAfter, Symbol detachReason)
        linkSettings.push(new RemoteLinkSettings("A", Duration.ofSeconds(20), LINK_DETACH_FORCED));
        linkSettings.push(new RemoteLinkSettings("B", Duration.ofSeconds(40), LINK_DETACH_FORCED));
        linkSettings.push(new RemoteLinkSettings("C", Duration.ofSeconds(50), LINK_DETACH_FORCED));

        final Flux<ReactorReceiver> receiverFlux = createReceiverFlux(linkSettings);
        final MessageFlux messageFlux = new MessageFlux(receiverFlux, prefetch, CreditFlowMode.RequestDriven, retryPolicy);

        // The SB ProcessorHandler.
        final Consumer<Message> messageHandler = new Consumer<Message>() {
            @Override
            public void accept(Message m) {
                String log = "    " + Thread.currentThread().getName() + " " + DATE_FORMAT.format(new Date()) + " : onNext";
                System.out.println(log);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // The SB ProcessorHandler wiring, INTERNAL to the Processor.
        messageFlux
            .flatMap(new Function<Message, Publisher<Message>>() {
                @Override
                public Publisher<Message> apply(Message message) {
                    return Mono.fromSupplier(() -> message)
                        .publishOn(Schedulers.boundedElastic())
                        .doOnNext(messageHandler);
                }
            }, concurrency, 1)
            .blockLast();
    }

    @Test
    public void batchReceiveWithTimeout() {
        final int prefetch = 500;
        final int maxBatchSize = 30;
        final Duration batchTimeout = Duration.ofSeconds(5);

        final Deque<RemoteLinkSettings> linkSettings = new ArrayDeque<>();
        linkSettings.push(new RemoteLinkSettings("A", Duration.ofSeconds(30), LINK_DETACH_FORCED));
        linkSettings.push(new RemoteLinkSettings("B", Duration.ofSeconds(20), LINK_DETACH_FORCED));
        linkSettings.push(new RemoteLinkSettings("C", Duration.ofSeconds(40), LINK_DETACH_FORCED));

        final Flux<ReactorReceiver> receiverFlux = createReceiverFlux(linkSettings);
        final MessageFlux messageFlux = new MessageFlux(receiverFlux, prefetch, CreditFlowMode.EmissionDriven, retryPolicy);

        // The EH ProcessorHandler.
        final Consumer<List<Message>> messageBatchHandler = new Consumer<List<Message>>() {
            @Override
            public void accept(List<Message> batch) {
                final String log = "    " + Thread.currentThread().getName() + " "
                    + DATE_FORMAT.format(new Date())
                    + ": onNext(batch) batch.size=" + batch.size();
                System.out.println(log);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // The EH ProcessorHandler wiring, INTERNAL to the Processor.
        messageFlux
            .windowTimeout(maxBatchSize, batchTimeout, true)
            .concatMap(Flux::collectList, 0)
            .publishOn(Schedulers.boundedElastic(), 1)
            .doOnNext(messageBatchHandler)
            .blockLast();
    }

    @Test
    public void batchReceiveWithTimeoutSlowMessageArrival() {
        final int prefetch = 100;
        final int maxBatchSize = 30;
        final Duration batchTimeout = Duration.ofSeconds(5);

        final Deque<RemoteLinkSettings> linkSettings = new ArrayDeque<>();
        linkSettings.push(new RemoteLinkSettings("A", Duration.ofSeconds(60), LINK_DETACH_FORCED, Duration.ofMillis(250)));
        linkSettings.push(new RemoteLinkSettings("B", Duration.ofSeconds(20), LINK_DETACH_FORCED));
        linkSettings.push(new RemoteLinkSettings("C", Duration.ofSeconds(40), LINK_DETACH_FORCED));

        final Flux<ReactorReceiver> receiverFlux = createReceiverFlux(linkSettings);
        final MessageFlux messageFlux = new MessageFlux(receiverFlux, prefetch, CreditFlowMode.EmissionDriven, retryPolicy);

        // The EH ProcessorHandler.
        final Consumer<List<Message>> messageBatchHandler = new Consumer<List<Message>>() {
            @Override
            public void accept(List<Message> batch) {
                final String log = "    " + Thread.currentThread().getName() + " "
                    + DATE_FORMAT.format(new Date())
                    + ": onNext(batch) batch.size=" + batch.size();
                System.out.println(log);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // The EH ProcessorHandler wiring, INTERNAL to the Processor.
        messageFlux
            .windowTimeout(maxBatchSize, batchTimeout, true)
            .concatMap(Flux::collectList, 0)
            .publishOn(Schedulers.boundedElastic(), 1)
            .doOnNext(messageBatchHandler)
            .blockLast();
    }

    private Flux<ReactorReceiver> createReceiverFlux(Deque<RemoteLinkSettings> linkSettings) {
        final AtomicReference<Scheduler> currentScheduler = new AtomicReference<>();
        final Runnable disposeCurrentScheduler = () -> {
            final Scheduler scheduler = currentScheduler.getAndSet(null);
            if (scheduler != null) {
                scheduler.dispose();
            }
        };
        final AmqpConnection connection = mock(AmqpConnection.class);
        when(connection.getShutdownSignals()).thenReturn(Flux.never());

        final Flux<ReactorReceiver> receiversFlux = Flux.create(sink -> sink.onRequest(r -> {
            Assertions.assertEquals(1, r);
            if (linkSettings.isEmpty()) {
                sink.complete();
            } else {
                disposeCurrentScheduler.run();
                RemoteLinkSettings settings = linkSettings.removeLast();
                System.out.println("\nReturning Next ReactorReceiver" + settings.getLinkIdFormatted());
                // Single-Threaded Scheduler like ProtonJ-Reactor-Thread.
                final Scheduler scheduler = Schedulers.newSingle("reactor_thread_" + settings.getLinkId());
                currentScheduler.set(scheduler);
                final ReactorDispatcher reactorDispatcher = createDispatcher();
                final ReceiveLinkHandler linkHandler = createReceiveLinkHandler(reactorDispatcher, settings.getLinkId());
                final MockRemoteLink remoteLink = new MockRemoteLink(BROKER_FQDN,
                    linkHandler,
                    scheduler,
                    settings);
                final ReactorReceiver reactorReceiver = createReactorReceiver(connection,
                    remoteLink,
                    linkHandler,
                    reactorDispatcher);
                remoteLink.attach();

                sink.next(reactorReceiver);
            }
        }));

        return receiversFlux.doFinally(__ -> {
            disposeCurrentScheduler.run();
        });
    }

    private ReactorDispatcher createDispatcher() {
        final ReactorDispatcher dispatcher = mock(ReactorDispatcher.class);
        try {
            doAnswer(invocation -> {
                final Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }).when(dispatcher).invoke(any(Runnable.class));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return dispatcher;
    }

    private ReceiveLinkHandler createReceiveLinkHandler(ReactorDispatcher dispatcher, String linkId) {
        final ReceiveLinkHandler handler = new ReceiveLinkHandler("CON_ID",
            BROKER_FQDN,
            linkId,
            ENTITY_PATH,
            DeliverySettleMode.SETTLE_ON_DELIVERY,
            dispatcher,
            retryOptions,
            false,
            metricsProvider);
        return handler;
    }

    private ReactorReceiver createReactorReceiver(AmqpConnection connection,
                                          MockRemoteLink broker,
                                          ReceiveLinkHandler handler,
                                          ReactorDispatcher dispatcher) {
        final Receiver receiver = mock(Receiver.class);
        doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            @SuppressWarnings("unchecked")
            final int credit = (int) args[0];
            broker.sendFlow(credit);
            return null;
        }).when(receiver).flow(anyInt());
        when(receiver.getLocalState()).thenReturn(EndpointState.ACTIVE);
        doNothing().when(receiver).close();
        doNothing().when(receiver).free();
        doNothing().when(receiver).setCondition(any());

        final TokenManager tokenManager = mock(TokenManager.class);
        when(tokenManager.getAuthorizationResults()).thenReturn(Flux.never());

        final ReactorReceiver reactorReceiver = new ReactorReceiver(connection,
            ENTITY_PATH,
            receiver,
            handler,
            tokenManager,
            dispatcher,
            retryOptions,
            metricsProvider);

        return reactorReceiver;
    }

    private static final class RemoteLinkSettings {
        private final String linkId;
        private final Duration detachAfter;
        private final Symbol detachReason;
        private final Duration eventDelay;

        RemoteLinkSettings(String linkId, Duration detachAfter, Symbol detachReason) {
            this.linkId = linkId;
            this.detachAfter = detachAfter;
            this.detachReason = detachReason;
            this.eventDelay = null;
        }

        RemoteLinkSettings(String linkId, Duration detachAfter, Symbol detachReason, Duration eventDelay) {
            this.linkId = linkId;
            this.detachAfter = detachAfter;
            this.detachReason = detachReason;
            this.eventDelay = eventDelay;
        }

        public String getLinkId() {
            return linkId;
        }

        Duration getDetachAfter() {
            return detachAfter;
        }

        Symbol getDetachReason() {
            return detachReason;
        }

        Duration getEventDelay() {
            return eventDelay;
        }

        String getLinkIdFormatted() {
            return " (linkId:" + this.linkId + ")";
        }
    }

    private static final class MockRemoteLink implements CoreSubscriber<Event> {
        private final String brokerFqdn;
        private final Scheduler singleThreadedScheduler;
        private final ReceiveLinkHandler handler;
        private final RemoteLinkSettings settings;
        private final long eventDelay;
        private Subscription messageBroker;
        private volatile boolean detached = false;

        MockRemoteLink(String brokerFqdn,
                       ReceiveLinkHandler handler,
                       Scheduler singleThreadedScheduler,
                       RemoteLinkSettings settings) {
            this.brokerFqdn = brokerFqdn;
            this.handler = handler;
            this.settings = settings;
            if (settings.getEventDelay() != null) {
                this.eventDelay = settings.getEventDelay().toMillis();
            } else {
                this.eventDelay = 0;
            }
            this.singleThreadedScheduler = singleThreadedScheduler;
        }

        void attach() {
            final CountDownLatch attachAwaiter = new CountDownLatch(1);
            this.singleThreadedScheduler.schedule(() -> {
                final Source remoteSource = new Source();
                remoteSource.setAddress(brokerFqdn);
                final Receiver linkToOpen = mock(Receiver.class);
                when(linkToOpen.getRemoteSource()).thenReturn(remoteSource);
                final Event remoteOpenEvent = mock(Event.class);
                when(remoteOpenEvent.getLink()).thenReturn(linkToOpen);

                handler.onLinkRemoteOpen(remoteOpenEvent);

                final Event deliveryEvent = deliveryEvent();
                Mono.fromSupplier(() -> deliveryEvent)
                    .repeat()
                    .publishOn(singleThreadedScheduler, 1)
                    .handle((BiConsumer<Event, SynchronousSink<Event>>) (event, synchronousSink) -> {
                        if (!detached) {
                            if (eventDelay != 0) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(eventDelay);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            synchronousSink.next(event);
                        } else {
                            final Receiver linkToDetach = mock(Receiver.class);
                            when(linkToDetach.getRemoteCondition())
                                .thenReturn(new ErrorCondition(settings.getDetachReason(), null));
                            when(linkToDetach.getLocalState()).thenReturn(EndpointState.ACTIVE);
                            doNothing().when(linkToDetach).setCondition(any());
                            doNothing().when(linkToDetach).close();
                            final Event remoteCloseEvent = mock(Event.class);
                            when(remoteCloseEvent.getLink()).thenReturn(linkToDetach);
                            handler.onLinkRemoteClose(remoteCloseEvent);
                            synchronousSink.complete();
                        }
                    })
                    .subscribe(this);
                attachAwaiter.countDown();
            });

            try {
                attachAwaiter.await(5, TimeUnit.SECONDS);
                System.out.println("    Attach completed" + settings.getLinkIdFormatted());
                Mono.delay(settings.getDetachAfter()).subscribe(__ -> {
                    System.out.println("    Requesting detach" + settings.getLinkIdFormatted());
                    detached = true;
                });
            } catch (InterruptedException e) {
                throw new RuntimeException("    Attach timeout" + settings.getLinkIdFormatted(), e);
            }
        }

        void sendFlow(int credit) {
            System.out.println("    Flow(" + credit + ")" + settings.getLinkIdFormatted());
            messageBroker.request(credit);
        }

        @Override
        public void onSubscribe(Subscription s) {
            if (Operators.validate(this.messageBroker, s)) {
                this.messageBroker = s;
            }
        }

        @Override
        public void onNext(Event deliveryEvent) {
            handler.onDelivery(deliveryEvent);
        }

        @Override
        public void onError(Throwable t) {
            // NOP
        }

        @Override
        public void onComplete() {
            // NOP
        }

        private Event deliveryEvent() {
            final byte[] deliveryContents = {
                0, 83, 115, -64, 15, 13, 64, 64, 64, 64, 64, 83, 1, 64, 64, 64,
                64, 64, 64, 64, 0, 83, 116, -63, 49, 4, -95, 11, 115, 116, 97, 116,
                117, 115, 45, 99, 111, 100, 101, 113, 0, 0, 0, -54, -95, 18, 115,
                116, 97, 116, 117, 115, 45, 100, 101, 115, 99, 114, 105, 112, 116,
                105, 111, 110, -95, 8, 65, 99, 99, 101, 112, 116, 101, 100
            };

            final Receiver receiver = mock(Receiver.class);
            final Delivery delivery = mock(Delivery.class);
            final int contentLength = deliveryContents.length;
            when(delivery.pending()).thenReturn(contentLength);
            when(delivery.getLink()).thenReturn(receiver);
            doNothing().when(delivery).settle();
            when(receiver.recv(any(), eq(0), eq(contentLength)))
                .thenAnswer(invocation -> {
                    final byte[] buffer = invocation.getArgument(0);
                    System.arraycopy(deliveryContents, 0, buffer, 0, contentLength);
                    return contentLength;
                });

            final Event event = mock(Event.class);
            when(event.getDelivery()).thenReturn(delivery);
            return event;
        }
    }
}
