// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** The AzureApplicationInsightsDataFeedPatch model. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataSourceType")
@JsonTypeName("AzureApplicationInsights")
@Fluent
public final class AzureApplicationInsightsDataFeedPatch extends DataFeedPatch {
    /*
     * The dataSourceParameter property.
     */
    @JsonProperty(value = "dataSourceParameter")
    private Option<AzureApplicationInsightsParameterPatch> dataSourceParameter;

    /**
     * Set the dataSourceParameter property: The dataSourceParameter property.
     *
     * @param dataSourceParameter the dataSourceParameter value to set.
     * @return the AzureApplicationInsightsDataFeedPatch object itself.
     */
    public AzureApplicationInsightsDataFeedPatch setDataSourceParameter(
            AzureApplicationInsightsParameterPatch dataSourceParameter) {
        if (dataSourceParameter == null) {
            this.dataSourceParameter = Option.empty();
        } else {
            this.dataSourceParameter = Option.of(dataSourceParameter);
        }
        return this;
    }
}
