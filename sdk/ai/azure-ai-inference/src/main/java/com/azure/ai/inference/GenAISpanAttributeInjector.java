// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.inference;

import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.function.BiFunction;

public interface GenAISpanAttributeInjector {
    BiFunction<Flux<ByteBuffer>, GenAISpanRequestAttributesWriter, Flux<ByteBuffer>> getRequestInjector();
    BiFunction<Flux<ByteBuffer>, GenAISpanResponseAttributesWriter, Flux<ByteBuffer>> getResponseInjector();
}
