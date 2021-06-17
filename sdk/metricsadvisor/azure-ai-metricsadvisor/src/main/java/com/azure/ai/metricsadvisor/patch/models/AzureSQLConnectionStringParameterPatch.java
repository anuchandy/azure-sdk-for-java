// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The AzureSQLConnectionStringParamPatch model. */
@Fluent
public final class AzureSQLConnectionStringParameterPatch {
    /*
     * The connection string to access the Azure SQL.
     */
    @JsonProperty(value = "connectionString")
    private Option<String> connectionString;

    /**
     * Set the connectionString property: The connection string to access the Azure SQL.
     *
     * @param connectionString the connectionString value to set.
     * @return the AzureSQLConnectionStringParamPatch object itself.
     */
    public AzureSQLConnectionStringParameterPatch setConnectionString(String connectionString) {
        if (connectionString == null) {
            this.connectionString = Option.empty();
        } else {
            this.connectionString = Option.of(connectionString);
        }
        return this;
    }
}
