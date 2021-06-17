// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.patch.implementation;

import com.azure.ai.metricsadvisor.implementation.models.AnomalyAlertingConfiguration;
import com.azure.ai.metricsadvisor.patch.models.AnomalyAlertingConfigurationPatch;
import com.azure.ai.metricsadvisor.implementation.models.AnomalyDetectionConfiguration;
import com.azure.ai.metricsadvisor.patch.models.AnomalyDetectionConfigurationPatch;
import com.azure.ai.metricsadvisor.implementation.models.DataFeedDetail;
import com.azure.ai.metricsadvisor.patch.models.DataFeedPatch;
import com.azure.ai.metricsadvisor.implementation.models.DataSourceCredential;
import com.azure.ai.metricsadvisor.patch.models.DatasourceCredentialEntityPatch;
import com.azure.ai.metricsadvisor.implementation.models.HookInfo;
import com.azure.ai.metricsadvisor.patch.models.NotificationHookPatch;
import com.azure.ai.metricsadvisor.models.MetricsAdvisorErrorCodeException;
import com.azure.core.annotation.BodyParam;
import com.azure.core.annotation.ExpectedResponses;
import com.azure.core.annotation.HeaderParam;
import com.azure.core.annotation.Host;
import com.azure.core.annotation.HostParam;
import com.azure.core.annotation.Patch;
import com.azure.core.annotation.PathParam;
import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceInterface;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.annotation.UnexpectedResponseExceptionType;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.http.policy.CookiePolicy;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.core.http.policy.UserAgentPolicy;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.RestProxy;
import com.azure.core.util.Context;
import com.azure.core.util.FluxUtil;
import com.azure.core.util.serializer.JacksonAdapter;
import com.azure.core.util.serializer.SerializerAdapter;
import reactor.core.publisher.Mono;

import java.util.UUID;

/** Initializes a new instance of the PatchImplClient type. */
public final class PatchImplClient {
    /** The proxy service used to perform REST calls. */
    private final PatchImplService service;

    /**
     * Supported Cognitive Services endpoints (protocol and hostname, for example:
     * https://&lt;resource-name&gt;.cognitiveservices.azure.com).
     */
    private final String endpoint;

    /**
     * Gets Supported Cognitive Services endpoints (protocol and hostname, for example:
     * https://&lt;resource-name&gt;.cognitiveservices.azure.com).
     *
     * @return the endpoint value.
     */
    public String getEndpoint() {
        return this.endpoint;
    }

    /** The HTTP pipeline to send requests through. */
    private final HttpPipeline httpPipeline;

    /**
     * Gets The HTTP pipeline to send requests through.
     *
     * @return the httpPipeline value.
     */
    public HttpPipeline getHttpPipeline() {
        return this.httpPipeline;
    }

    /** The serializer to serialize an object into a string. */
    private final SerializerAdapter serializerAdapter;

    /**
     * Gets The serializer to serialize an object into a string.
     *
     * @return the serializerAdapter value.
     */
    public SerializerAdapter getSerializerAdapter() {
        return this.serializerAdapter;
    }

    /**
     * Initializes an instance of PatchImplClient client.
     *
     * @param endpoint Supported Cognitive Services endpoints (protocol and hostname, for example:
     *     https://&lt;resource-name&gt;.cognitiveservices.azure.com).
     */
    PatchImplClient(String endpoint) {
        this(
                new HttpPipelineBuilder()
                        .policies(new UserAgentPolicy(), new RetryPolicy(), new CookiePolicy())
                        .build(),
                JacksonAdapter.createDefaultSerializerAdapter(),
                endpoint);
    }

    /**
     * Initializes an instance of PatchImplClient client.
     *
     * @param httpPipeline The HTTP pipeline to send requests through.
     * @param serializerAdapter The serializer to serialize an object into a string.
     * @param endpoint Supported Cognitive Services endpoints (protocol and hostname, for example:
     *     https://&lt;resource-name&gt;.cognitiveservices.azure.com).
     */
    public PatchImplClient(
            HttpPipeline httpPipeline, SerializerAdapter serializerAdapter, String endpoint) {
        this.httpPipeline = httpPipeline;
        this.serializerAdapter = serializerAdapter;
        this.endpoint = endpoint;
        this.service =
                RestProxy.create(
                        PatchImplService.class,
                        this.httpPipeline,
                        this.getSerializerAdapter());
    }

    /**
     * The interface defining all the services for PatchImplClient to be used by the
     * proxy service to perform REST calls.
     */
    @Host("{endpoint}/metricsadvisor/v1.0")
    @ServiceInterface(name = "PatchImplService")
    private interface PatchImplService {
        @Patch("/alert/anomaly/configurations/{configurationId}")
        @ExpectedResponses({200})
        @UnexpectedResponseExceptionType(MetricsAdvisorErrorCodeException.class)
        Mono<Response<AnomalyAlertingConfiguration>> updateAnomalyAlertingConfiguration(
                @HostParam("endpoint") String endpoint,
                @PathParam("configurationId") UUID configurationId,
                @BodyParam("application/merge-patch+json") AnomalyAlertingConfigurationPatch body,
                @HeaderParam("Accept") String accept,
                Context context);

        @Patch("/enrichment/anomalyDetection/configurations/{configurationId}")
        @ExpectedResponses({200})
        @UnexpectedResponseExceptionType(MetricsAdvisorErrorCodeException.class)
        Mono<Response<AnomalyDetectionConfiguration>> updateAnomalyDetectionConfiguration(
                @HostParam("endpoint") String endpoint,
                @PathParam("configurationId") UUID configurationId,
                @BodyParam("application/merge-patch+json") AnomalyDetectionConfigurationPatch body,
                @HeaderParam("Accept") String accept,
                Context context);

        @Patch("/credentials/{credentialId}")
        @ExpectedResponses({200})
        @UnexpectedResponseExceptionType(MetricsAdvisorErrorCodeException.class)
        Mono<Response<DataSourceCredential>> updateCredential(
                @HostParam("endpoint") String endpoint,
                @PathParam("credentialId") UUID credentialId,
                @BodyParam("application/merge-patch+json") DatasourceCredentialEntityPatch body,
                @HeaderParam("Accept") String accept,
                Context context);

        @Patch("/dataFeeds/{dataFeedId}")
        @ExpectedResponses({200})
        @UnexpectedResponseExceptionType(MetricsAdvisorErrorCodeException.class)
        Mono<Response<DataFeedDetail>> updateDataFeed(
                @HostParam("endpoint") String endpoint,
                @PathParam("dataFeedId") UUID dataFeedId,
                @BodyParam("application/merge-patch+json") DataFeedPatch body,
                @HeaderParam("Accept") String accept,
                Context context);

        @Patch("/hooks/{hookId}")
        @ExpectedResponses({200})
        @UnexpectedResponseExceptionType(MetricsAdvisorErrorCodeException.class)
        Mono<Response<HookInfo>> updateHook(
                @HostParam("endpoint") String endpoint,
                @PathParam("hookId") UUID hookId,
                @BodyParam("application/merge-patch+json") NotificationHookPatch body,
                @HeaderParam("Accept") String accept,
                Context context);
    }

    /**
     * Update anomaly alerting configuration.
     *
     * @param configurationId anomaly alerting configuration unique id.
     * @param body anomaly alerting configuration.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<AnomalyAlertingConfiguration>> updateAnomalyAlertingConfigurationWithResponseAsync(
            UUID configurationId, AnomalyAlertingConfigurationPatch body) {
        final String accept = "application/json";
        return FluxUtil.withContext(
                context ->
                        service.updateAnomalyAlertingConfiguration(
                                this.getEndpoint(), configurationId, body, accept, context));
    }

    /**
     * Update anomaly alerting configuration.
     *
     * @param configurationId anomaly alerting configuration unique id.
     * @param body anomaly alerting configuration.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<AnomalyAlertingConfiguration>> updateAnomalyAlertingConfigurationWithResponseAsync(
            UUID configurationId, AnomalyAlertingConfigurationPatch body, Context context) {
        final String accept = "application/json";
        return service.updateAnomalyAlertingConfiguration(this.getEndpoint(), configurationId, body, accept, context);
    }

    /**
     * Update anomaly alerting configuration.
     *
     * @param configurationId anomaly alerting configuration unique id.
     * @param body anomaly alerting configuration.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<AnomalyAlertingConfiguration> updateAnomalyAlertingConfigurationAsync(
            UUID configurationId, AnomalyAlertingConfigurationPatch body) {
        return updateAnomalyAlertingConfigurationWithResponseAsync(configurationId, body)
                .flatMap(
                        (Response<AnomalyAlertingConfiguration> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update anomaly alerting configuration.
     *
     * @param configurationId anomaly alerting configuration unique id.
     * @param body anomaly alerting configuration.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<AnomalyAlertingConfiguration> updateAnomalyAlertingConfigurationAsync(
            UUID configurationId, AnomalyAlertingConfigurationPatch body, Context context) {
        return updateAnomalyAlertingConfigurationWithResponseAsync(configurationId, body, context)
                .flatMap(
                        (Response<AnomalyAlertingConfiguration> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update anomaly alerting configuration.
     *
     * @param configurationId anomaly alerting configuration unique id.
     * @param body anomaly alerting configuration.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public AnomalyAlertingConfiguration updateAnomalyAlertingConfiguration(
            UUID configurationId, AnomalyAlertingConfigurationPatch body) {
        return updateAnomalyAlertingConfigurationAsync(configurationId, body).block();
    }

    /**
     * Update anomaly alerting configuration.
     *
     * @param configurationId anomaly alerting configuration unique id.
     * @param body anomaly alerting configuration.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<AnomalyAlertingConfiguration> updateAnomalyAlertingConfigurationWithResponse(
            UUID configurationId, AnomalyAlertingConfigurationPatch body, Context context) {
        return updateAnomalyAlertingConfigurationWithResponseAsync(configurationId, body, context).block();
    }

    /**
     * Update anomaly detection configuration.
     *
     * @param configurationId anomaly detection configuration unique id.
     * @param body anomaly detection configuration.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<AnomalyDetectionConfiguration>> updateAnomalyDetectionConfigurationWithResponseAsync(
            UUID configurationId, AnomalyDetectionConfigurationPatch body) {
        final String accept = "application/json";
        return FluxUtil.withContext(
                context ->
                        service.updateAnomalyDetectionConfiguration(
                                this.getEndpoint(), configurationId, body, accept, context));
    }

    /**
     * Update anomaly detection configuration.
     *
     * @param configurationId anomaly detection configuration unique id.
     * @param body anomaly detection configuration.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<AnomalyDetectionConfiguration>> updateAnomalyDetectionConfigurationWithResponseAsync(
            UUID configurationId, AnomalyDetectionConfigurationPatch body, Context context) {
        final String accept = "application/json";
        return service.updateAnomalyDetectionConfiguration(this.getEndpoint(), configurationId, body, accept, context);
    }

    /**
     * Update anomaly detection configuration.
     *
     * @param configurationId anomaly detection configuration unique id.
     * @param body anomaly detection configuration.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<AnomalyDetectionConfiguration> updateAnomalyDetectionConfigurationAsync(
            UUID configurationId, AnomalyDetectionConfigurationPatch body) {
        return updateAnomalyDetectionConfigurationWithResponseAsync(configurationId, body)
                .flatMap(
                        (Response<AnomalyDetectionConfiguration> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update anomaly detection configuration.
     *
     * @param configurationId anomaly detection configuration unique id.
     * @param body anomaly detection configuration.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<AnomalyDetectionConfiguration> updateAnomalyDetectionConfigurationAsync(
            UUID configurationId, AnomalyDetectionConfigurationPatch body, Context context) {
        return updateAnomalyDetectionConfigurationWithResponseAsync(configurationId, body, context)
                .flatMap(
                        (Response<AnomalyDetectionConfiguration> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update anomaly detection configuration.
     *
     * @param configurationId anomaly detection configuration unique id.
     * @param body anomaly detection configuration.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public AnomalyDetectionConfiguration updateAnomalyDetectionConfiguration(
            UUID configurationId, AnomalyDetectionConfigurationPatch body) {
        return updateAnomalyDetectionConfigurationAsync(configurationId, body).block();
    }

    /**
     * Update anomaly detection configuration.
     *
     * @param configurationId anomaly detection configuration unique id.
     * @param body anomaly detection configuration.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<AnomalyDetectionConfiguration> updateAnomalyDetectionConfigurationWithResponse(
            UUID configurationId, AnomalyDetectionConfigurationPatch body, Context context) {
        return updateAnomalyDetectionConfigurationWithResponseAsync(configurationId, body, context).block();
    }

    /**
     * Update a data source credential.
     *
     * @param credentialId Data source credential unique ID.
     * @param body Update data source credential request.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<DataSourceCredential>> updateCredentialWithResponseAsync(
            UUID credentialId, DatasourceCredentialEntityPatch body) {
        final String accept = "application/json";
        return FluxUtil.withContext(
                context -> service.updateCredential(this.getEndpoint(), credentialId, body, accept, context));
    }

    /**
     * Update a data source credential.
     *
     * @param credentialId Data source credential unique ID.
     * @param body Update data source credential request.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<DataSourceCredential>> updateCredentialWithResponseAsync(
        UUID credentialId, DatasourceCredentialEntityPatch body, Context context) {
        final String accept = "application/json";
        return service.updateCredential(this.getEndpoint(), credentialId, body, accept, context);
    }

    /**
     * Update a data source credential.
     *
     * @param credentialId Data source credential unique ID.
     * @param body Update data source credential request.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<DataSourceCredential> updateCredentialAsync(UUID credentialId, DatasourceCredentialEntityPatch body) {
        return updateCredentialWithResponseAsync(credentialId, body)
                .flatMap(
                        (Response<DataSourceCredential> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update a data source credential.
     *
     * @param credentialId Data source credential unique ID.
     * @param body Update data source credential request.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<DataSourceCredential> updateCredentialAsync(
        UUID credentialId, DatasourceCredentialEntityPatch body, Context context) {
        return updateCredentialWithResponseAsync(credentialId, body, context)
                .flatMap(
                        (Response<DataSourceCredential> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update a data source credential.
     *
     * @param credentialId Data source credential unique ID.
     * @param body Update data source credential request.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public DataSourceCredential updateCredential(UUID credentialId, DatasourceCredentialEntityPatch body) {
        return updateCredentialAsync(credentialId, body).block();
    }

    /**
     * Update a data source credential.
     *
     * @param credentialId Data source credential unique ID.
     * @param body Update data source credential request.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<DataSourceCredential> updateCredentialWithResponse(
        UUID credentialId, DatasourceCredentialEntityPatch body, Context context) {
        return updateCredentialWithResponseAsync(credentialId, body, context).block();
    }

    /**
     * Update a data feed.
     *
     * @param dataFeedId The data feed unique id.
     * @param body parameters to update a data feed.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<DataFeedDetail>> updateDataFeedWithResponseAsync(UUID dataFeedId, DataFeedPatch body) {
        final String accept = "application/json";
        return FluxUtil.withContext(
                context -> service.updateDataFeed(this.getEndpoint(), dataFeedId, body, accept, context));
    }

    /**
     * Update a data feed.
     *
     * @param dataFeedId The data feed unique id.
     * @param body parameters to update a data feed.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<DataFeedDetail>> updateDataFeedWithResponseAsync(
        UUID dataFeedId, DataFeedPatch body, Context context) {
        final String accept = "application/json";
        return service.updateDataFeed(this.getEndpoint(), dataFeedId, body, accept, context);
    }

    /**
     * Update a data feed.
     *
     * @param dataFeedId The data feed unique id.
     * @param body parameters to update a data feed.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<DataFeedDetail> updateDataFeedAsync(UUID dataFeedId, DataFeedPatch body) {
        return updateDataFeedWithResponseAsync(dataFeedId, body)
                .flatMap(
                        (Response<DataFeedDetail> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update a data feed.
     *
     * @param dataFeedId The data feed unique id.
     * @param body parameters to update a data feed.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<DataFeedDetail> updateDataFeedAsync(UUID dataFeedId, DataFeedPatch body, Context context) {
        return updateDataFeedWithResponseAsync(dataFeedId, body, context)
                .flatMap(
                        (Response<DataFeedDetail> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update a data feed.
     *
     * @param dataFeedId The data feed unique id.
     * @param body parameters to update a data feed.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public DataFeedDetail updateDataFeed(UUID dataFeedId, DataFeedPatch body) {
        return updateDataFeedAsync(dataFeedId, body).block();
    }

    /**
     * Update a data feed.
     *
     * @param dataFeedId The data feed unique id.
     * @param body parameters to update a data feed.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<DataFeedDetail> updateDataFeedWithResponse(
        UUID dataFeedId, DataFeedPatch body, Context context) {
        return updateDataFeedWithResponseAsync(dataFeedId, body, context).block();
    }

    /**
     * Update a hook.
     *
     * @param hookId Hook unique ID.
     * @param body Update hook request.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<HookInfo>> updateHookWithResponseAsync(UUID hookId, NotificationHookPatch body) {
        final String accept = "application/json";
        return FluxUtil.withContext(context -> service.updateHook(this.getEndpoint(), hookId, body, accept, context));
    }

    /**
     * Update a hook.
     *
     * @param hookId Hook unique ID.
     * @param body Update hook request.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Response<HookInfo>> updateHookWithResponseAsync(UUID hookId, NotificationHookPatch body, Context context) {
        final String accept = "application/json";
        return service.updateHook(this.getEndpoint(), hookId, body, accept, context);
    }

    /**
     * Update a hook.
     *
     * @param hookId Hook unique ID.
     * @param body Update hook request.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<HookInfo> updateHookAsync(UUID hookId, NotificationHookPatch body) {
        return updateHookWithResponseAsync(hookId, body)
                .flatMap(
                        (Response<HookInfo> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update a hook.
     *
     * @param hookId Hook unique ID.
     * @param body Update hook request.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<HookInfo> updateHookAsync(UUID hookId, NotificationHookPatch body, Context context) {
        return updateHookWithResponseAsync(hookId, body, context)
                .flatMap(
                        (Response<HookInfo> res) -> {
                            if (res.getValue() != null) {
                                return Mono.just(res.getValue());
                            } else {
                                return Mono.empty();
                            }
                        });
    }

    /**
     * Update a hook.
     *
     * @param hookId Hook unique ID.
     * @param body Update hook request.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public HookInfo updateHook(UUID hookId, NotificationHookPatch body) {
        return updateHookAsync(hookId, body).block();
    }

    /**
     * Update a hook.
     *
     * @param hookId Hook unique ID.
     * @param body Update hook request.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws MetricsAdvisorErrorCodeException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<HookInfo> updateHookWithResponse(UUID hookId, NotificationHookPatch body, Context context) {
        return updateHookWithResponseAsync(hookId, body, context).block();
    }
}
