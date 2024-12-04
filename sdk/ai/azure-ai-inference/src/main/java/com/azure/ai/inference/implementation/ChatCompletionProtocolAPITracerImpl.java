package com.azure.ai.inference.implementation;

import com.azure.ai.inference.ChatCompletionProtocolAPITraceSupport;
import com.azure.ai.inference.ChatCompletionProtocolAPITraceSupport.GenAISpanAttributeInjector;
import com.azure.ai.inference.ChatCompletionProtocolAPITraceSupport.GenAISpanRequestAttributesWriter;
import com.azure.ai.inference.ChatCompletionProtocolAPITraceSupport.GenAISpanResponseAttributesWriter;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.tracing.SpanKind;
import com.azure.core.util.tracing.StartSpanOptions;
import com.azure.core.util.tracing.Tracer;
import com.azure.core.util.tracing.TracerProvider;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.BiFunction;

public final class ChatCompletionProtocolAPITracerImpl implements ChatCompletionProtocolAPITracer {
    private final String skipTraceUUID;
    private final ChatCompletionProtocolAPITraceSupport support;
    private final Tracer tracer;

    ChatCompletionProtocolAPITracerImpl(String skipTraceUUID) {
        this.skipTraceUUID = skipTraceUUID;
        this.support = tryLoadSupport();
        if (this.support != null) {
            this.tracer = TracerProvider.getDefaultProvider().createTracer(CLIENT_NAME, CLIENT_VERSION, "Azure.AI", null);
        } else {
            this.tracer = null;
        }
    }

    @Override
    public Response<BinaryData> traceCompleteWithResponse(CompleteWithResponseOperation operation, BinaryData completeRequest, RequestOptions requestOptions) {
        if (!isEnabled() || skipTrace(requestOptions)) {
            return operation.invoke(completeRequest, requestOptions);
        }

        final GenAISpanAttributeInjector spanAttrInjector = support.completeWithResponseInjector(completeRequest, requestOptions);
        final BiFunction<BinaryData, GenAISpanRequestAttributesWriter, BinaryData> reqAttrInjector = spanAttrInjector.getRequestInjector();
        final BiFunction<BinaryData, GenAISpanResponseAttributesWriter, BinaryData> resAttrInjector = spanAttrInjector.getResponseInjector();

        final Context span = tracer.start("chat", new StartSpanOptions(SpanKind.CLIENT), Context.NONE);
        final GenAISpanRequestAttributesWriter spanRequestAttributesWriter = new GenAISpanRequestAttributesWriter(tracer, span);
        final GenAISpanResponseAttributesWriter spanResponseAttributesWriter = new GenAISpanResponseAttributesWriter(tracer, span);

        final BinaryData completeRequest0 = reqAttrInjector.apply(completeRequest, spanRequestAttributesWriter);
        try {
            final Response<BinaryData> response = operation.invoke(completeRequest0, requestOptions.setContext(span));
            final BinaryData rValue = resAttrInjector.apply(response.getValue(), spanResponseAttributesWriter);
            return response(response, rValue);
        } catch (Exception e) {
            tracer.end(null, e, span);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private static ChatCompletionProtocolAPITraceSupport tryLoadSupport() {
        final ServiceLoader<ChatCompletionProtocolAPITraceSupport.Provider> serviceLoader = ServiceLoader
            .load(ChatCompletionProtocolAPITraceSupport.Provider.class, ChatCompletionProtocolAPITraceSupport.class.getClassLoader());
        final Iterator<ChatCompletionProtocolAPITraceSupport.Provider> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            final ChatCompletionProtocolAPITraceSupport.Provider provider = iterator.next();
            return provider.create();
        }
        return null;
    }

    private boolean isEnabled() {
        return tracer != null && tracer.isEnabled();
    }

    private boolean skipTrace(RequestOptions requestOptions) {
        if (requestOptions.getContext() == null) {
            return false;
        }
        return requestOptions.getContext().getData(skipTraceUUID).isPresent();
    }

    private Response<BinaryData> response(Response<BinaryData> original, BinaryData value) {
        return new SimpleResponse<BinaryData>(original.getRequest(), original.getStatusCode(), original.getHeaders(), value);
    }
}
