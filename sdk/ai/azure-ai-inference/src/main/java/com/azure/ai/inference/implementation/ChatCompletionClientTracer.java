// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.inference.implementation;

import com.azure.ai.inference.models.ChatChoice;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatCompletionsToolCall;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRole;
import com.azure.ai.inference.models.CompletionsFinishReason;
import com.azure.ai.inference.models.CompletionsUsage;
import com.azure.ai.inference.models.StreamingChatChoiceUpdate;
import com.azure.ai.inference.models.StreamingChatCompletionsUpdate;
import com.azure.ai.inference.models.StreamingChatResponseMessageUpdate;
import com.azure.ai.inference.models.StreamingChatResponseToolCallUpdate;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.logging.ClientLogger;
import com.azure.core.util.tracing.SpanKind;
import com.azure.core.util.tracing.StartSpanOptions;
import com.azure.core.util.tracing.Tracer;
import com.azure.core.util.tracing.TracerProvider;
import com.azure.json.JsonProviders;
import com.azure.json.JsonWriter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Tracing for convenience (handwritten) methods in {@link com.azure.ai.inference.ChatCompletionsClient}.
 */
public final class ChatCompletionClientTracer {
    private final Map<String, String> PROPERTIES = CoreUtils.getProperties("azure-ai-inference.properties");
    private final String CLIENT_NAME = PROPERTIES.getOrDefault("name", "UnknownName");
    private final String CLIENT_VERSION = PROPERTIES.getOrDefault("version", "UnknownVersion");
    private final String INFERENCE_GEN_AI_SYSTEM_NAME = "az.ai.inference";

    private static final String FINISH_REASON_ERROR = "{\"finish_reason\": \"error\"}";
    private static final String FINISH_REASON_CANCELED = "{\"finish_reason\": \"canceled\"}";
    private final ClientLogger logger;
    private final boolean traceContent; // make it configurable (com.azure.core.util.Configuration?)
    private final URL endpoint;
    private final Tracer tracer;

    //<editor-fold desc="Call contracts">

    /**
     * Reference to the operation performing the actual completion call.
     */
    @FunctionalInterface
    public interface CompleteOperation {
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
     * Reference to the operation performing the actual completion streaming call.
     */
    @FunctionalInterface
    public interface StreamingCompleteOperation {
        /**
         * invokes the operation.
         *
         * @param completeRequest The completeRequest parameter for the {@code operation}.
         * @param requestOptions The requestOptions parameter for the {@code operation}.
         * @return chat completions streaming for the provided chat messages.
         */
        Flux<StreamingChatCompletionsUpdate> invoke(BinaryData completeRequest, RequestOptions requestOptions);
    }

    //</editor-fold>

    /**
     * Creates ChatCompletionClientTracer.
     *
     * @param endpoint the service endpoint.
     */
    public ChatCompletionClientTracer(String endpoint) {
        this.logger = new ClientLogger(ChatCompletionClientTracer.class);
        this.endpoint = parse(endpoint, logger);
        this.traceContent = true;
        this.tracer = TracerProvider.getDefaultProvider().createTracer(CLIENT_NAME, CLIENT_VERSION, "Azure.AI", null);
    }

    /**
     * Traces the convenience API - {@link com.azure.ai.inference.ChatCompletionsClient#complete(ChatCompletionsOptions)} API.
     *
     * @param request input options containing chat options for complete API.
     * @param operation the operation performing the actual completion call.
     * @param completeRequest The completeRequest parameter for the {@code operation}.
     * @param requestOptions The requestOptions parameter for the {@code operation}.
     * @return chat completions for the provided chat messages.
     */
    public ChatCompletions traceComplete(ChatCompletionsOptions request, CompleteOperation operation,
        BinaryData completeRequest, RequestOptions requestOptions) {
        if (!tracer.isEnabled()) {
            return operation.invoke(completeRequest, requestOptions);
        }
        final Context span = tracer.start(rootSpanName(request), new StartSpanOptions(SpanKind.CLIENT), Context.NONE);
        traceCompletionRequestAttributes(request, span);
        traceCompletionRequestEvents(request.getMessages(), span);

        try (AutoCloseable ignored = tracer.makeSpanCurrent(span)) {
            final ChatCompletions response = operation.invoke(completeRequest, requestOptions.setContext(span));
            traceCompletionResponseAttributes(response, ignored, span);
            traceCompletionResponseEvents(response, span);
            tracer.end(null, null, span);
            return response;
        } catch (Exception e) {
            tracer.end(null, e, span);
            throw logger.logExceptionAsError(asRuntimeException(e));
        }
    }

    /**
     * Traces the convenience API - {@link com.azure.ai.inference.ChatCompletionsClient#completeStream(ChatCompletionsOptions)} API.
     *
     * @param request input options containing chat options for complete streaming API.
     * @param operation the operation performing the actual streaming completion call.
     * @param completeRequest The completeRequest parameter for the {@code operation}.
     * @param requestOptions The requestOptions parameter for the {@code operation}.
     * @return chat completions streaming for the provided chat messages.
     */
    public Flux<StreamingChatCompletionsUpdate> traceStreamingCompletion(ChatCompletionsOptions request,
        StreamingCompleteOperation operation, BinaryData completeRequest, RequestOptions requestOptions) {
        if (!tracer.isEnabled()) {
            return operation.invoke(completeRequest, requestOptions);
        }
        final StreamingChatCompletionsState state
            = new StreamingChatCompletionsState(traceContent, request, operation, completeRequest, requestOptions);

        final Mono<StreamingChatCompletionsState> resourceSupplier = Mono.fromSupplier(() -> {
            final StreamingChatCompletionsState resource = state;

            final Context span
                = tracer.start(rootSpanName(resource.request), new StartSpanOptions(SpanKind.CLIENT), Context.NONE);
            traceCompletionRequestAttributes(resource.request, span);
            traceCompletionRequestEvents(resource.request.getMessages(), span);
            return resource.setSpan(span);
        });

        final Function<StreamingChatCompletionsState, Flux<StreamingChatCompletionsUpdate>> resourceClosure
            = resource -> {
                final RequestOptions rOptions = resource.requestOptions.setContext(resource.span);
                final Flux<StreamingChatCompletionsUpdate> completionChunks
                    = resource.operation.invoke(resource.completeRequest, rOptions);
                return completionChunks.doOnNext(resource::onNextChunk);
            };

        final Function<StreamingChatCompletionsState, Mono<Void>> asyncComplete = resource -> {
            final Context span = resource.span;
            final StreamingChatCompletionsUpdate lastChunk = resource.lastChunk;
            final String finishReasons = resource.getFinishReasons();

            traceCompletionResponseAttributes(lastChunk, finishReasons, span);
            traceChoiceEvent(resource.toJson(logger), OffsetDateTime.now(ZoneOffset.UTC), span);
            tracer.end(null, null, span);
            return Mono.empty();
        };

        final BiFunction<StreamingChatCompletionsState, Throwable, Mono<Void>> asyncError = (resource, throwable) -> {
            final Context span = resource.span;

            tracer.setAttribute("error.type", throwable.getClass().getName(), span);
            traceChoiceEvent(FINISH_REASON_ERROR, OffsetDateTime.now(ZoneOffset.UTC), span);
            tracer.end(null, throwable, span);
            return Mono.empty();
        };

        final Function<StreamingChatCompletionsState, Mono<Void>> asyncCancel = resource -> {
            final Context span = resource.span;
            tracer.setAttribute("error.type", "canceled", span); // check with Liudmila, is it correct to record canceled as an 'error'?
            traceChoiceEvent(FINISH_REASON_CANCELED, OffsetDateTime.now(ZoneOffset.UTC), span);
            tracer.end(null, new RuntimeException("canceled"), span);
            return Mono.empty();
        };

        return Flux.usingWhen(resourceSupplier, resourceClosure, asyncComplete, asyncError, asyncCancel);
    }

    //<editor-fold desc="Private util types, methods">

    private String rootSpanName(ChatCompletionsOptions completeRequest) {
        return CoreUtils.isNullOrEmpty(completeRequest.getModel()) ? "chat" : "chat " + completeRequest.getModel();
    }

    private void traceCompletionRequestAttributes(ChatCompletionsOptions request, Context span) {
        final String modelId = request.getModel();
        tracer.setAttribute("gen_ai.operation.name", "chat", span);
        tracer.setAttribute("gen_ai.system", INFERENCE_GEN_AI_SYSTEM_NAME, span);
        tracer.setAttribute("gen_ai.request.model", CoreUtils.isNullOrEmpty(modelId) ? "chat" : modelId, span);
        if (request.getMaxTokens() != null) {
            tracer.setAttribute("gen_ai.request.max_tokens", request.getMaxTokens(), span);
        }
        if (request.getTemperature() != null) {
            tracer.setAttribute("gen_ai.request.temperature", request.getTemperature(), span);
        }
        if (request.getTopP() != null) {
            tracer.setAttribute("gen_ai.request.top_p", request.getTopP(), span);
        }
        if (endpoint != null) {
            tracer.setAttribute("server.address", endpoint.getHost(), span);
            if (endpoint.getPort() != 443) {
                tracer.setAttribute("server.port", endpoint.getPort(), span);
            }
        }
    }

    private void traceCompletionRequestEvents(List<ChatRequestMessage> messages, Context span) {
        if (!traceContent) {
            return;
        }
        if (messages != null) {
            for (ChatRequestMessage message : messages) {
                final ChatRole role = message.getRole();
                if (role != null) {
                    final String eventName = "gen_ai." + role.getValue() + ".message";
                    final String eventContent = toJsonString(message);
                    if (eventContent != null) {
                        final Map<String, Object> eventAttributes = new HashMap<>(2);
                        eventAttributes.put("gen_ai.system", INFERENCE_GEN_AI_SYSTEM_NAME);
                        eventAttributes.put("gen_ai.event.content", eventContent);
                        tracer.addEvent(eventName, eventAttributes, OffsetDateTime.now(ZoneOffset.UTC), span);
                    }
                }
            }
        }
    }

    private void traceCompletionResponseAttributes(ChatCompletions response, AutoCloseable ignored, Context span) {
        tracer.setAttribute("gen_ai.response.id", response.getId(), span);
        tracer.setAttribute("gen_ai.response.model", response.getModel(), span);
        final CompletionsUsage usage = response.getUsage();
        if (usage != null) {
            tracer.setAttribute("gen_ai.usage.input_tokens", usage.getPromptTokens(), span);
            tracer.setAttribute("gen_ai.usage.output_tokens", usage.getCompletionTokens(), span);
        }
        final List<ChatChoice> choices = response.getChoices();
        if (choices != null) {
            tracer.setAttribute("gen_ai.response.finish_reasons", getFinishReasons(choices), span);
        }
    }

    private void traceCompletionResponseAttributes(StreamingChatCompletionsUpdate response, String finishReasons,
        Context span) {
        tracer.setAttribute("gen_ai.response.id", response.getId(), span);
        tracer.setAttribute("gen_ai.response.model", response.getModel(), span);
        final CompletionsUsage usage = response.getUsage();
        if (usage != null) {
            tracer.setAttribute("gen_ai.usage.input_tokens", usage.getPromptTokens(), span);
            tracer.setAttribute("gen_ai.usage.output_tokens", usage.getCompletionTokens(), span);
        }
        tracer.setAttribute("gen_ai.response.finish_reasons", finishReasons, span);
    }

    private void traceCompletionResponseEvents(ChatCompletions response, Context span) {
        final List<ChatChoice> choices = response.getChoices();
        if (choices != null) {
            final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            for (ChatChoice choice : choices) {
                traceChoiceEvent(toJsonString(choice), now, span);
            }
        }
    }

    private void traceChoiceEvent(String choiceContent, OffsetDateTime timestamp, Context span) {
        final Map<String, Object> eventAttributes = new HashMap<>(2);
        eventAttributes.put("gen_ai.system", INFERENCE_GEN_AI_SYSTEM_NAME);
        eventAttributes.put("gen_ai.event.content", choiceContent);
        tracer.addEvent("gen_ai.choice", eventAttributes, timestamp, span);
    }

    private String toJsonString(ChatRequestMessage message) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
            JsonWriter writer = JsonProviders.createWriter(stream)) {
            message.toJson(writer);
            writer.flush();
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.atWarning().log("'ChatRequestMessage' serialization error", e);
        }
        return null;
    }

    private String toJsonString(ChatChoice choice) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
            JsonWriter writer = JsonProviders.createWriter(stream)) {
            writer.writeStartObject();
            writer.writeStartObject("message");
            if (traceContent) {
                writer.writeStringField("content", choice.getMessage().getContent());
            }
            if (choice.getMessage() != null) {
                final List<ChatCompletionsToolCall> toolCalls = choice.getMessage().getToolCalls();
                if (toolCalls != null && !toolCalls.isEmpty()) {
                    writer.writeStartArray("tool_calls");
                    for (ChatCompletionsToolCall toolCall : toolCalls) {
                        if (traceContent) {
                            toolCall.toJson(writer);
                        } else {
                            writer.writeStartObject();
                            writer.writeStringField("id", toolCall.getId());
                            writer.writeStringField("type", toolCall.getType());
                            writer.writeEndObject();
                        }
                    }
                    writer.writeEndArray();
                }
            }
            writer.writeEndObject();
            final CompletionsFinishReason finishReason = choice.getFinishReason();
            if (finishReason != null) {
                writer.writeStringField("finish_reason", finishReason.getValue());
            }
            writer.writeIntField("index", choice.getIndex());
            writer.writeEndObject();
            writer.flush();
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.atWarning().log("'ChatChoice' serialization error", e);
        }
        return null;
    }

    private static String getFinishReasons(List<ChatChoice> choices) {
        final StringBuilder finishReasons = new StringBuilder("[");
        for (ChatChoice choice : choices) {
            final CompletionsFinishReason finishReason = choice.getFinishReason();
            if (finishReason == null) {
                finishReasons.append("none");
            } else {
                finishReasons.append(finishReason.getValue());
            }
            finishReasons.append(", ");
        }
        finishReasons.append("]");
        return finishReasons.toString();
    }

    private static URL parse(String endpoint, ClientLogger logger) {
        if (CoreUtils.isNullOrEmpty(endpoint)) {
            return null;
        }
        try {
            final URI uri = new URI(endpoint);
            return uri.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            logger.atWarning().log("service endpoint uri parse error.", e);
        }
        return null;
    }

    private static RuntimeException asRuntimeException(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

    private static final class StreamingChatCompletionsState {
        private final boolean traceContent;
        private final ChatCompletionsOptions request;
        private final StreamingCompleteOperation operation;
        private final BinaryData completeRequest;
        private final RequestOptions requestOptions;
        // mutable part of the state to accumulate partial data from Completion chunks.
        private final StringBuilder content;
        private final ArrayDeque<StreamingChatResponseToolCallUpdate> toolCalls; // uses Dequeue to release slots once consumed.
        private final ArrayDeque<String> toolCallIds;
        private final ArrayDeque<CompletionsFinishReason> finishReasons;
        //
        private Context span;
        private StreamingChatCompletionsUpdate lastChunk;
        private CompletionsFinishReason finishReason;
        private int index;

        StreamingChatCompletionsState(boolean traceContent, ChatCompletionsOptions request,
            StreamingCompleteOperation operation, BinaryData completeRequest, RequestOptions requestOptions) {
            this.traceContent = traceContent;
            this.request = request;
            this.operation = operation;
            this.completeRequest = completeRequest;
            this.requestOptions = requestOptions;
            this.content = new StringBuilder();
            this.toolCalls = new ArrayDeque<>();
            this.toolCallIds = new ArrayDeque<>();
            this.finishReasons = new ArrayDeque<>();
        }

        StreamingChatCompletionsState setSpan(Context context) {
            this.span = context;
            return this;
        }

        void onNextChunk(StreamingChatCompletionsUpdate chunk) {
            this.lastChunk = chunk;
            final List<StreamingChatChoiceUpdate> choices = chunk.getChoices();
            if (choices == null || choices.isEmpty()) {
                return;
            }
            for (StreamingChatChoiceUpdate choice : choices) {
                this.finishReason = choice.getFinishReason();
                this.index = choice.getIndex();
                if (choice.getFinishReason() != null) {
                    this.finishReasons.add(choice.getFinishReason());
                }
                final StreamingChatResponseMessageUpdate delta = choice.getDelta();
                if (delta == null) {
                    continue;
                }
                final List<StreamingChatResponseToolCallUpdate> toolCalls = delta.getToolCalls();
                if (this.traceContent) {
                    if (delta.getContent() != null) {
                        this.content.append(delta.getContent());
                    }
                    if (toolCalls != null) {
                        this.toolCalls.addAll(toolCalls);
                    }
                } else {
                    if (toolCalls != null) {
                        final List<String> ids = toolCalls.stream()
                            .map(StreamingChatResponseToolCallUpdate::getId)
                            .collect(Collectors.toList());
                        this.toolCallIds.addAll(ids);
                    }
                }
            }
        }

        String toJson(ClientLogger logger) {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
                JsonWriter writer = JsonProviders.createWriter(stream)) {
                writer.writeStartObject();
                writer.writeStartObject("message");
                if (this.traceContent) {
                    writer.writeStringField("content", this.content.toString());
                    writer.writeStartArray("tool_calls");
                    StreamingChatResponseToolCallUpdate toolCall;
                    while ((toolCall = this.toolCalls.poll()) != null) {
                        toolCall.toJson(writer);
                    }
                    writer.writeEndArray();
                } else {
                    writer.writeStartArray("tool_calls");
                    String toolCallId;
                    while ((toolCallId = this.toolCallIds.poll()) != null) {
                        writer.writeStartObject();
                        writer.writeStringField("id", toolCallId);
                        writer.writeEndObject();
                    }
                    writer.writeEndArray();
                }
                writer.writeEndObject();
                if (this.finishReason != null) {
                    writer.writeStringField("finish_reason", this.finishReason.getValue());
                }
                writer.writeIntField("index", this.index);
                writer.writeEndObject();
                writer.flush();
                return new String(stream.toByteArray(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.atWarning().log("'StreamingChatCompletionsState' serialization error", e);
            }
            return null;
        }

        String getFinishReasons() {
            final StringBuilder finishReasonsSb = new StringBuilder("[");
            CompletionsFinishReason reason;
            while ((reason = finishReasons.poll()) != null) {
                finishReasonsSb.append(reason.getValue());
                finishReasonsSb.append(", ");
            }
            finishReasonsSb.append("]");
            return finishReasonsSb.toString();
        }
    }

    //</editor-fold>
}
