// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** The AzureDataExplorerDataFeedPatch model. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataSourceType")
@JsonTypeName("AzureDataExplorer")
@Fluent
public final class AzureDataExplorerDataFeedPatch extends DataFeedPatch {
    /*
     * The dataSourceParameter property.
     */
    @JsonProperty(value = "dataSourceParameter")
    private Option<SqlSourceParameterPatch> dataSourceParameter;

    /**
     * Set the dataSourceParameter property: The dataSourceParameter property.
     *
     * @param dataSourceParameter the dataSourceParameter value to set.
     * @return the AzureDataExplorerDataFeedPatch object itself.
     */
    public AzureDataExplorerDataFeedPatch setDataSourceParameter(SqlSourceParameterPatch dataSourceParameter) {
        if (dataSourceParameter == null) {
            this.dataSourceParameter = Option.empty();
        } else {
            this.dataSourceParameter = Option.of(dataSourceParameter);
        }
        return this;
    }
}
