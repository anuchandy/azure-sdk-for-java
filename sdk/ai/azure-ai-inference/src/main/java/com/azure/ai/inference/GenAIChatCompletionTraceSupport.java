// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.inference;

import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;

public interface GenAIChatCompletionTraceSupport {
    interface Provider {
        GenAIChatCompletionTraceSupport create();
    }

    enum ProtocolAPI {
        /**
         * {@link ChatCompletionsClient#completeWithResponse(BinaryData, RequestOptions)}.
         */
        COMPLETE_WITH_RESPONSE_BINARY_DATA_REQUEST_OPTIONS,
        /**
         * {@link ChatCompletionsClient#getModelInfoWithResponse(RequestOptions)}.
         */
        GET_MODEL_INFO_WITH_RESPONSE_REQUEST_OPTIONS
    }

    GenAISpanAttributeInjector getInjector(ProtocolAPI api);
}
