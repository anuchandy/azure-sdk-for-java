// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The SQLSourceParameterPatch model. */
@Fluent
public final class SqlSourceParameterPatch {
    /*
     * The connection string of this database
     */
    @JsonProperty(value = "connectionString")
    private Option<String> connectionString;

    /*
     * The script to query this database
     */
    @JsonProperty(value = "query")
    private Option<String> query;

    /**
     * Set the connectionString property: The connection string of this database.
     *
     * @param connectionString the connectionString value to set.
     * @return the SQLSourceParameterPatch object itself.
     */
    public SqlSourceParameterPatch setConnectionString(String connectionString) {
        if (connectionString == null) {
            this.connectionString = Option.empty();
        } else {
            this.connectionString = Option.of(connectionString);
        }
        return this;
    }

    /**
     * Set the query property: The script to query this database.
     *
     * @param query the query value to set.
     * @return the SQLSourceParameterPatch object itself.
     */
    public SqlSourceParameterPatch setQuery(String query) {
        if (query == null) {
            this.query = Option.empty();
        } else {
            this.query = Option.of(query);
        }
        return this;
    }
}
