// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The AzureApplicationInsightsParameterPatch model. */
@Fluent
public final class AzureApplicationInsightsParameterPatch {
    /*
     * The Azure cloud that this Azure Application Insights in
     */
    @JsonProperty(value = "azureCloud")
    private Option<String> azureCloud;

    /*
     * The application id of this Azure Application Insights
     */
    @JsonProperty(value = "applicationId")
    private Option<String> applicationId;

    /*
     * The API Key that can access this Azure Application Insights
     */
    @JsonProperty(value = "apiKey")
    private Option<String> apiKey;

    /*
     * The statement to query this Azure Application Insights
     */
    @JsonProperty(value = "query")
    private Option<String> query;

    /**
     * Set the azureCloud property: The Azure cloud that this Azure Application Insights in.
     *
     * @param azureCloud the azureCloud value to set.
     * @return the AzureApplicationInsightsParameterPatch object itself.
     */
    public AzureApplicationInsightsParameterPatch setAzureCloud(String azureCloud) {
        if (azureCloud == null) {
            this.azureCloud = Option.empty();
        } else {
            this.azureCloud = Option.of(azureCloud);
        }
        return this;
    }

    /**
     * Set the applicationId property: The application id of this Azure Application Insights.
     *
     * @param applicationId the applicationId value to set.
     * @return the AzureApplicationInsightsParameterPatch object itself.
     */
    public AzureApplicationInsightsParameterPatch setApplicationId(String applicationId) {
        if (applicationId == null) {
            this.applicationId = Option.empty();
        } else {
            this.applicationId = Option.of(applicationId);
        }
        return this;
    }

    /**
     * Set the apiKey property: The API Key that can access this Azure Application Insights.
     *
     * @param apiKey the apiKey value to set.
     * @return the AzureApplicationInsightsParameterPatch object itself.
     */
    public AzureApplicationInsightsParameterPatch setApiKey(String apiKey) {
        if (apiKey == null) {
            this.apiKey = Option.empty();
        } else {
            this.apiKey = Option.of(apiKey);
        }
        return this;
    }

    /**
     * Set the query property: The statement to query this Azure Application Insights.
     *
     * @param query the query value to set.
     * @return the AzureApplicationInsightsParameterPatch object itself.
     */
    public AzureApplicationInsightsParameterPatch setQuery(String query) {
        if (query == null) {
            this.query = Option.empty();
        } else {
            this.query = Option.of(query);
        }
        return this;
    }
}
