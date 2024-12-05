// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.inference;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipelineCallContext;
import com.azure.core.http.HttpPipelineNextPolicy;
import com.azure.core.http.HttpPipelineNextSyncPolicy;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.FluxUtil;
import com.azure.core.util.logging.ClientLogger;
import com.azure.core.util.tracing.SpanKind;
import com.azure.core.util.tracing.StartSpanOptions;
import com.azure.core.util.tracing.Tracer;
import com.azure.core.util.tracing.TracerProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.azure.core.util.tracing.Tracer.DISABLE_TRACING_KEY;

public class GenAITracerPolicy implements HttpPipelinePolicy {
    private static final ClientLogger LOGGER = new ClientLogger(GenAITracerPolicy.class);
    private final TraceSupport traceSupport;
    private final Tracer tracer;

    public GenAITracerPolicy() {
        this.traceSupport = TraceSupport.load();
        if (this.traceSupport != null) {
            final Map<String, String> properties = CoreUtils.getProperties("azure-ai-inference.properties");
            final String clientName = properties.getOrDefault("name", "UnknownName");
            final String clientVersion = properties.getOrDefault("version", "UnknownVersion");
            this.tracer = TracerProvider.getDefaultProvider().createTracer(clientName, clientVersion, "Azure.AI", null);
        } else {
            this.tracer = null;
        }
    }

    @Override
    public Mono<HttpResponse> process(HttpPipelineCallContext context, HttpPipelineNextPolicy next) {
        if (!isDisabled(context)) {
            return next.process();
        }
        final TraceSupport.Result r = traceSupport.tryGet(context);
        if (r == null) {
            return next.process();
        }

        final BiFunction<Flux<ByteBuffer>, GenAISpanRequestAttributesWriter, Flux<ByteBuffer>> injector0 = r.getSpanRequestAttributeInjector();
        final BiFunction<Flux<ByteBuffer>, GenAISpanResponseAttributesWriter, Flux<ByteBuffer>> injector1 = r.getSpanResponseAttributeInjector();

        final HttpRequest request = context.getHttpRequest();

        final Mono<Context> resourceSupplier = Mono.fromSupplier(() -> {
            final Context span = tracer.start(r.name, new StartSpanOptions(SpanKind.CLIENT), Context.NONE);
            final Flux<ByteBuffer> body = injector0.apply(request.getBody(), new GenAISpanRequestAttributesWriter(tracer, span, context.getContext()));
            context.setHttpRequest(new HttpRequest(request.getHttpMethod(), request.getUrl(), request.getHeaders(), body));
            return span;
        });

        final Function<Context, Mono<HttpResponse>> sourceSupplier = (span) -> {
            return next.process().map(response -> {
                final Flux<ByteBuffer> body = injector1.apply(response.getBody(), new GenAISpanResponseAttributesWriter(tracer, span, context.getContext()));
                return new Response(response, body);
            });
        };

        final Function<Context, Mono<Void>> asyncComplete = span -> {
            // No span closing here, span is closed when GenAISpanRequestAttributesWriter.write(..) is called.
            return Mono.empty();
        };

        final BiFunction<Context, Throwable, Mono<Void>> asyncError = (span, throwable) -> {
            tracer.end(null, throwable, span);
            return Mono.empty();
        };

        final Function<Context, Mono<Void>> asyncCancel = span -> {
            tracer.end(null, new RuntimeException("canceled"), span);
            return Mono.empty();
        };

        return Mono.usingWhen(resourceSupplier, sourceSupplier, asyncComplete, asyncError, asyncCancel);
    }

    @Override
    public HttpResponse processSync(HttpPipelineCallContext context, HttpPipelineNextSyncPolicy next) {
        if (isDisabled(context)) {
            return next.processSync();
        }
        final TraceSupport.Result r = traceSupport.tryGet(context);
        if (r == null) {
            return next.processSync();
        }
        final BiFunction<Flux<ByteBuffer>, GenAISpanRequestAttributesWriter, Flux<ByteBuffer>> injector0 = r.getSpanRequestAttributeInjector();
        final BiFunction<Flux<ByteBuffer>, GenAISpanResponseAttributesWriter, Flux<ByteBuffer>> injector1 = r.getSpanResponseAttributeInjector();

        final HttpRequest request = context.getHttpRequest();
        final Context span = tracer.start(r.name, new StartSpanOptions(SpanKind.CLIENT), Context.NONE);
        final Flux<ByteBuffer> bodyReq = injector0.apply(request.getBody(), new GenAISpanRequestAttributesWriter(tracer, span, context.getContext()));
        context.setHttpRequest(new HttpRequest(request.getHttpMethod(), request.getUrl(), request.getHeaders(), bodyReq));

        try (AutoCloseable scope = tracer.makeSpanCurrent(span)) {
            final HttpResponse response = next.processSync();
            final Flux<ByteBuffer> bodyRes = injector1.apply(response.getBody(), new GenAISpanResponseAttributesWriter(tracer, span, context.getContext()));
            return new Response(response, bodyRes);
        } catch (RuntimeException ex) {
            tracer.end(null, ex, span);
            throw ex;
        } catch (Exception ex) {
            tracer.end(null, ex, span);
            throw LOGGER.logExceptionAsWarning(new RuntimeException(ex));
        }
    }

    private boolean isDisabled(HttpPipelineCallContext context) {
        return tracer != null && tracer.isEnabled() && !((boolean) context.getData(DISABLE_TRACING_KEY).orElse(false));
    }

    private static final class TraceSupport {
        private final GenAIChatCompletionTraceSupport chatTraceSupport;

        static TraceSupport load() {
            final GenAIChatCompletionTraceSupport chatTraceSupport = tryLoadChatSupport();
            // load other inference trace support (e.g., ImageEmbedding) when there is.
            if (chatTraceSupport != null) {
                return new TraceSupport(chatTraceSupport /*, other trace supports, */);
            }
            return null;
        }

        Result tryGet(HttpPipelineCallContext context) {
            if (chatTraceSupport != null) {
                if (isChatCompletionCall(context)) {
                    final GenAISpanAttributeInjector injector
                        = chatTraceSupport.getInjector(GenAIChatCompletionTraceSupport.ProtocolAPI.COMPLETE_WITH_RESPONSE_BINARY_DATA_REQUEST_OPTIONS);
                    if (injector != null) {
                        return new Result("chat", injector);
                    }
                } else if (isGetModelInfoCall(context)) {
                    final GenAISpanAttributeInjector injector
                        = chatTraceSupport.getInjector(GenAIChatCompletionTraceSupport.ProtocolAPI.GET_MODEL_INFO_WITH_RESPONSE_REQUEST_OPTIONS);
                    if (injector != null) {
                        return new Result("chat", injector);
                    }
                }
            }
            // other GenAI inference tracers.
            return null;
        }

        private TraceSupport(GenAIChatCompletionTraceSupport chatTraceSupport) {
            this.chatTraceSupport = chatTraceSupport;
        }

        private boolean isChatCompletionCall(HttpPipelineCallContext context) {
            final HttpRequest request = context.getHttpRequest();
            final URL url = request.getUrl();
            final String path = url.getPath();
            return request.getHttpMethod() == HttpMethod.POST
                && path.startsWith("/openai/deployments/") && path.endsWith("/info");
        }

        private boolean isGetModelInfoCall(HttpPipelineCallContext context) {
            final HttpRequest request = context.getHttpRequest();
            final URL url = request.getUrl();
            final String path = url.getPath();
            return request.getHttpMethod() == HttpMethod.POST
                && path.startsWith("/openai/deployments/") && path.endsWith("/chat/completions");
        }

        private static GenAIChatCompletionTraceSupport tryLoadChatSupport() {
            final ServiceLoader<GenAIChatCompletionTraceSupport.Provider> serviceLoader = ServiceLoader
                .load(GenAIChatCompletionTraceSupport.Provider.class, GenAIChatCompletionTraceSupport.class.getClassLoader());
            final Iterator<GenAIChatCompletionTraceSupport.Provider> itr = serviceLoader.iterator();
            if (itr.hasNext()) {
                final GenAIChatCompletionTraceSupport.Provider provider = itr.next();
                return provider.create();
            }
            return null;
        }

        private static final class Result {
            private final String name;
            private final GenAISpanAttributeInjector injector;

            Result(String name, GenAISpanAttributeInjector injector) {
                this.name = name;
                this.injector = injector;
            }

            BiFunction<Flux<ByteBuffer>, GenAISpanRequestAttributesWriter, Flux<ByteBuffer>> getSpanRequestAttributeInjector() {
                return this.injector.getRequestInjector();
            }

            BiFunction<Flux<ByteBuffer>, GenAISpanResponseAttributesWriter, Flux<ByteBuffer>> getSpanResponseAttributeInjector() {
                return this.injector.getResponseInjector();
            }
        }
    }

    private static final class Response extends HttpResponse {
        private final HttpResponse response;
        private final Flux<ByteBuffer> body;

        Response(HttpResponse response, Flux<ByteBuffer> body) {
            super(response.getRequest());
            this.response = response;
            this.body = body;
        }

        @Override
        public int getStatusCode() {
            return response.getStatusCode();
        }

        @Deprecated
        @Override
        public String getHeaderValue(String name) {
            return response.getHeaderValue(name);
        }

        @Override
        public String getHeaderValue(HttpHeaderName headerName) {
            return response.getHeaderValue(headerName);
        }

        @Override
        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }

        @Override
        public Flux<ByteBuffer> getBody() {
            return this.body;
        }

        @Override
        public Mono<byte[]> getBodyAsByteArray() {
            return FluxUtil.collectBytesInByteBufferStream(this.getBody());
        }

        @Override
        public Mono<String> getBodyAsString() {
            return this.getBodyAsByteArray().map((bytes) -> CoreUtils.bomAwareToString(bytes, getCharset()));
        }

        @Override
        public BinaryData getBodyAsBinaryData() {
            throw new RuntimeException("???"); // API to create BinaryData from Flux<ByteBuffer> (this.body) is internal.
        }

        @Override
        public Mono<String> getBodyAsString(Charset charset) {
            return this.getBodyAsByteArray().map((bytes) -> CoreUtils.bomAwareToString(bytes, charset.toString()));
        }

        @Override
        public Mono<InputStream> getBodyAsInputStream() {
            throw new RuntimeException("???");
        }

        @Override
        public void close() {
            response.close();
        }

        private String getCharset() {
            return this.getHeaderValue(HttpHeaderName.CONTENT_TYPE);
        }
    }
}
