// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The AzureEventHubsParameterPatch model. */
@Fluent
public final class AzureEventHubsParameterPatch {
    /*
     * The connection string of this Azure Event Hubs
     */
    @JsonProperty(value = "connectionString")
    private Option<String> connectionString;

    /*
     * The consumer group to be used in this data feed
     */
    @JsonProperty(value = "consumerGroup")
    private Option<String> consumerGroup;

    /**
     * Set the connectionString property: The connection string of this Azure Event Hubs.
     *
     * @param connectionString the connectionString value to set.
     * @return the AzureEventHubsParameterPatch object itself.
     */
    public AzureEventHubsParameterPatch setConnectionString(String connectionString) {
        if (connectionString == null) {
            this.connectionString = Option.empty();
        } else {
            this.connectionString = Option.of(connectionString);
        }
        return this;
    }

    /**
     * Set the consumerGroup property: The consumer group to be used in this data feed.
     *
     * @param consumerGroup the consumerGroup value to set.
     * @return the AzureEventHubsParameterPatch object itself.
     */
    public AzureEventHubsParameterPatch setConsumerGroup(String consumerGroup) {
        if (consumerGroup == null) {
            this.consumerGroup = Option.empty();
        } else {
            this.consumerGroup = Option.of(consumerGroup);
        }
        return this;
    }
}
