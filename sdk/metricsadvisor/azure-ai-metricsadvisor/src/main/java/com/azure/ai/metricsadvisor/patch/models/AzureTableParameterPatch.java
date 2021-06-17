// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The AzureTableParameterPatch model. */
@Fluent
public final class AzureTableParameterPatch {
    /*
     * The connection string of this Azure Table
     */
    @JsonProperty(value = "connectionString")
    private Option<String> connectionString;

    /*
     * A table name in this Azure Table
     */
    @JsonProperty(value = "table")
    private Option<String> table;

    /*
     * The statement to query this table. Please find syntax and details from
     * Azure Table documents.
     */
    @JsonProperty(value = "query")
    private Option<String> query;

    /**
     * Set the connectionString property: The connection string of this Azure Table.
     *
     * @param connectionString the connectionString value to set.
     * @return the AzureTableParameterPatch object itself.
     */
    public AzureTableParameterPatch setConnectionString(String connectionString) {
        if (connectionString == null) {
            this.connectionString = Option.empty();
        } else {
            this.connectionString = Option.of(connectionString);
        }
        return this;
    }

    /**
     * Set the table property: A table name in this Azure Table.
     *
     * @param table the table value to set.
     * @return the AzureTableParameterPatch object itself.
     */
    public AzureTableParameterPatch setTable(String table) {
        if (table == null) {
            this.table = Option.empty();
        } else {
            this.table = Option.of(table);
        }
        return this;
    }

    /**
     * Set the query property: The statement to query this table. Please find syntax and details from Azure Table
     * documents.
     *
     * @param query the query value to set.
     * @return the AzureTableParameterPatch object itself.
     */
    public AzureTableParameterPatch setQuery(String query) {
        if (query == null) {
            this.query = Option.empty();
        } else {
            this.query = Option.of(query);
        }
        return this;
    }
}
