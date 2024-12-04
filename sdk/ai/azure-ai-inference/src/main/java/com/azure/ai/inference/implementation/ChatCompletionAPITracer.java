// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.inference.implementation;

import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.StreamingChatCompletionsUpdate;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;
import com.azure.core.util.CoreUtils;
import reactor.core.publisher.Flux;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Tracing contract for {@link com.azure.ai.inference.ChatCompletionsClient}.
 */
public interface ChatCompletionAPITracer {
    Map<String, String> PROPERTIES = CoreUtils.getProperties("azure-ai-inference.properties");
    String CLIENT_NAME = PROPERTIES.getOrDefault("name", "UnknownName");
    String CLIENT_VERSION = PROPERTIES.getOrDefault("version", "UnknownVersion");
    String INFERENCE_GEN_AI_SYSTEM_NAME = "az.ai.inference";
    ChatCompletionAPITracer NOP = new NOP();

    /**
     * Service loader provider to create an implementation of {@link ChatCompletionAPITracer}.
     */
    interface Provider {
        /**
         * Creates an instance of  {@link ChatCompletionAPITracer} implementation.
         *
         * @param endpoint the service endpoint.
         * @return the ChatCompletionClientTracer implementation instance.
         */
        ChatCompletionAPITracer create(String endpoint);
    }

    /**
     * loads an implementation of {@link ChatCompletionAPITracer}.
     *
     * @param endpoint the service endpoint.
     * @return the {@link ChatCompletionAPITracer} implementation instance.
     */
    static ChatCompletionAPITracer load(String endpoint) {
        final ServiceLoader<ChatCompletionAPITracer.Provider> serviceLoader = ServiceLoader
            .load(ChatCompletionAPITracer.Provider.class, ChatCompletionAPITracer.class.getClassLoader());
        final Iterator<ChatCompletionAPITracer.Provider> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            final Provider provider = iterator.next();
            return provider.create(endpoint);
        }
        return NOP;
    }

    /**
     * Traces the {@link com.azure.ai.inference.ChatCompletionsClient#complete(ChatCompletionsOptions)} API.
     *
     * @param request input options containing chat options for complete API.
     * @param operation the operation performing the actual completion call.
     * @param completeRequest The completeRequest parameter for the {@code operation}.
     * @param requestOptions The requestOptions parameter for the {@code operation}.
     * @return chat completions for the provided chat messages.
     */
    ChatCompletions traceComplete(ChatCompletionsOptions request, CompleteOperation operation,
        BinaryData completeRequest, RequestOptions requestOptions);

    /**
     * Reference to the operation performing the actual completion call.
     */
    @FunctionalInterface
    interface CompleteOperation {
        /**
         * invokes the operation.
         *
         * @param completeRequest The completeRequest parameter for the {@code operation}.
         * @param requestOptions The requestOptions parameter for the {@code operation}.
         * @return chat completions for the provided chat messages.
         */
        ChatCompletions invoke(BinaryData completeRequest, RequestOptions requestOptions);
    }

    /**
     * Traces the {@link com.azure.ai.inference.ChatCompletionsClient#completeStream(ChatCompletionsOptions)} API.
     *
     * @param request input options containing chat options for complete streaming API.
     * @param operation the operation performing the actual streaming completion call.
     * @param completeRequest The completeRequest parameter for the {@code operation}.
     * @param requestOptions The requestOptions parameter for the {@code operation}.
     * @return chat completions streaming for the provided chat messages.
     */
    Flux<StreamingChatCompletionsUpdate> traceStreamingCompletion(ChatCompletionsOptions request,
        StreamingCompleteOperation operation, BinaryData completeRequest, RequestOptions requestOptions);

    /**
     * Reference to the operation performing the actual completion streaming call.
     */
    @FunctionalInterface
    interface StreamingCompleteOperation {
        /**
         * invokes the operation.
         *
         * @param completeRequest The completeRequest parameter for the {@code operation}.
         * @param requestOptions The requestOptions parameter for the {@code operation}.
         * @return chat completions streaming for the provided chat messages.
         */
        Flux<StreamingChatCompletionsUpdate> invoke(BinaryData completeRequest, RequestOptions requestOptions);
    }

    /**
     * A no operation implementation for the {@link ChatCompletionAPITracer} contract.
     */
    final class NOP implements ChatCompletionAPITracer {
        @Override
        public ChatCompletions traceComplete(ChatCompletionsOptions request, CompleteOperation operation,
            BinaryData completeRequest, RequestOptions requestOptions) {
            return operation.invoke(completeRequest, requestOptions);
        }

        @Override
        public Flux<StreamingChatCompletionsUpdate> traceStreamingCompletion(ChatCompletionsOptions request,
            StreamingCompleteOperation operation, BinaryData completeRequest, RequestOptions requestOptions) {
            return operation.invoke(completeRequest, requestOptions);
        }
    }
}
