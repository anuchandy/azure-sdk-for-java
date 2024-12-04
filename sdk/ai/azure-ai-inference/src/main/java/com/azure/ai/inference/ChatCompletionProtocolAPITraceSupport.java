// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.inference;

import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.tracing.Tracer;

import java.util.function.BiFunction;

public interface ChatCompletionProtocolAPITraceSupport {
    interface Provider {
        ChatCompletionProtocolAPITraceSupport create();
    }

    GenAISpanAttributeInjector completeWithResponseInjector(BinaryData completeRequest, RequestOptions requestOptions);

    interface GenAISpanAttributeInjector {
        BiFunction<BinaryData, GenAISpanRequestAttributesWriter, BinaryData> getRequestInjector();
        BiFunction<BinaryData, GenAISpanResponseAttributesWriter, BinaryData> getResponseInjector();
    }

    public class GenAISpanRequestAttributesWriter {
        private final Tracer tracer;
        private final Context span;

        public GenAISpanRequestAttributesWriter(Tracer tracer, Context span) {
            this.tracer = tracer;
            this.span = span;
        }

        public void write(GenAIRequestAttributes attributes) {
            if (attributes.modelId != null) {
                tracer.setAttribute("gen_ai.request.model", attributes.modelId, span);
            }
            if (attributes.maxTokens != null) {
                tracer.setAttribute("gen_ai.request.max_tokens", attributes.maxTokens, span);
            }
            if (attributes.temperature != null) {
                tracer.setAttribute("gen_ai.request.temperature", attributes.temperature, span);
            }
            if (attributes.topP != null) {
                tracer.setAttribute("gen_ai.request.top_p", attributes.topP, span);
            }
        }

        public static final class GenAIRequestAttributes {
            private String modelId;
            private Integer maxTokens;
            private Double temperature;
            private Double topP;

            public GenAIRequestAttributes setModelId(String modelId) {
                this.modelId = modelId;
                return this;
            }

            public GenAIRequestAttributes setMaxTokens(int maxTokens) {
                this.maxTokens = maxTokens;
                return this;
            }

            public GenAIRequestAttributes setTemperature(double temperature) {
                this.temperature = temperature;
                return this;
            }

            public GenAIRequestAttributes setTopP(double topP) {
                this.topP = topP;
                return this;
            }
        }
    }

    public class GenAISpanResponseAttributesWriter {
        private final Tracer tracer;
        private final Context span;

        public GenAISpanResponseAttributesWriter(Tracer tracer, Context span) {
            this.tracer = tracer;
            this.span = span;
        }

        public void write(GenAIResponseAttributes attributes) {
            if (attributes.id != null) {
                tracer.setAttribute("gen_ai.response.id", attributes.id, span);
            }
            if (attributes.model != null) {
                tracer.setAttribute("gen_ai.response.model", attributes.model, span);
            }
            if (attributes.promptTokens != null) {
                tracer.setAttribute("gen_ai.usage.input_tokens", attributes.promptTokens, span);
            }
            if (attributes.promptTokens != null) {
                tracer.setAttribute("gen_ai.usage.output_tokens", attributes.completionTokens, span);
            }
            if (attributes.finishReasons != null) {
                tracer.setAttribute("gen_ai.response.finish_reasons", attributes.finishReasons, span);
            }
            tracer.end(null, null, span);
        }

        public static final class GenAIResponseAttributes {
            private String id;
            private String model;
            private Integer promptTokens;
            private Integer completionTokens;
            private String finishReasons;

            public GenAIResponseAttributes setId(String id) {
                this.id = id;
                return this;
            }

            public GenAIResponseAttributes setModel(String model) {
                this.model = model;
                return this;
            }

            public GenAIResponseAttributes setPromptTokens(int promptTokens) {
                this.promptTokens = promptTokens;
                return this;
            }

            public GenAIResponseAttributes setCompletionTokens(int completionTokens) {
                this.completionTokens = completionTokens;
                return this;
            }

            public GenAIResponseAttributes setFinishReasons(String finishReasons) {
                this.finishReasons = finishReasons;
                return this;
            }
        }
    }
}
