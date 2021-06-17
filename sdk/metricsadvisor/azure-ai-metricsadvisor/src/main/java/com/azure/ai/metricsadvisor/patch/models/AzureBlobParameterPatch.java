// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The AzureBlobParameterPatch model. */
@Fluent
public final class AzureBlobParameterPatch {
    /*
     * The connection string of this Azure Blob
     */
    @JsonProperty(value = "connectionString")
    private Option<String> connectionString;

    /*
     * The container name in this Azure Blob
     */
    @JsonProperty(value = "container")
    private Option<String> container;

    /*
     * The path template in this container
     */
    @JsonProperty(value = "blobTemplate")
    private Option<String> blobTemplate;

    /**
     * Set the connectionString property: The connection string of this Azure Blob.
     *
     * @param connectionString the connectionString value to set.
     * @return the AzureBlobParameterPatch object itself.
     */
    public AzureBlobParameterPatch setConnectionString(String connectionString) {
        if (connectionString == null) {
            this.connectionString = Option.empty();
        } else {
            this.connectionString = Option.of(connectionString);
        }
        return this;
    }

    /**
     * Set the container property: The container name in this Azure Blob.
     *
     * @param container the container value to set.
     * @return the AzureBlobParameterPatch object itself.
     */
    public AzureBlobParameterPatch setContainer(String container) {
        if (container == null) {
            this.container = Option.empty();
        } else {
            this.container = Option.of(container);
        }
        return this;
    }

    /**
     * Set the blobTemplate property: The path template in this container.
     *
     * @param blobTemplate the blobTemplate value to set.
     * @return the AzureBlobParameterPatch object itself.
     */
    public AzureBlobParameterPatch setBlobTemplate(String blobTemplate) {
        if (blobTemplate == null) {
            this.blobTemplate = Option.empty();
        } else {
            this.blobTemplate = Option.of(blobTemplate);
        }
        return this;
    }
}
