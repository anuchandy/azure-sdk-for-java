// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.inference;

import com.azure.core.util.Context;
import com.azure.core.util.tracing.Tracer;

public final class GenAISpanRequestAttributesWriter {
    private final Tracer tracer;
    private final Context span;
    private final Context callContext;

    public GenAISpanRequestAttributesWriter(Tracer tracer, Context span, Context callContext) {
        this.tracer = tracer;
        this.span = span;
        this.callContext = callContext;
    }

    public Context getCallContext() {
        return callContext;
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
