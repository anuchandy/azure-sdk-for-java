// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** The AzureBlobDataFeedPatch model. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataSourceType")
@JsonTypeName("AzureBlob")
@Fluent
public final class AzureBlobDataFeedPatch extends DataFeedPatch {
    /*
     * The dataSourceParameter property.
     */
    @JsonProperty(value = "dataSourceParameter")
    private Option<AzureBlobParameterPatch> dataSourceParameter;

    /**
     * Set the dataSourceParameter property: The dataSourceParameter property.
     *
     * @param dataSourceParameter the dataSourceParameter value to set.
     * @return the AzureBlobDataFeedPatch object itself.
     */
    public AzureBlobDataFeedPatch setDataSourceParameter(AzureBlobParameterPatch dataSourceParameter) {
        if (dataSourceParameter == null) {
            this.dataSourceParameter = Option.empty();
        } else {
            this.dataSourceParameter = Option.of(dataSourceParameter);
        }
        return this;
    }
}
