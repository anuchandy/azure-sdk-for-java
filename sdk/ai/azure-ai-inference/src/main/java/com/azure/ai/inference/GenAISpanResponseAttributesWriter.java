// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.inference;

import com.azure.core.util.Context;
import com.azure.core.util.tracing.Tracer;

public final class GenAISpanResponseAttributesWriter {
    private final Tracer tracer;
    private final Context span;
    private final Context callContext;

    public GenAISpanResponseAttributesWriter(Tracer tracer, Context span, Context callContext) {
        this.tracer = tracer;
        this.span = span;
        this.callContext = callContext;
    }

    public Context getCallContext() {
        return callContext;
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
