// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The AzureCosmosDBParameterPatch model. */
@Fluent
public final class AzureCosmosDbParameterPatch {
    /*
     * The connection string of this Azure CosmosDB
     */
    @JsonProperty(value = "connectionString")
    private Option<String> connectionString;

    /*
     * The statement to query this collection
     */
    @JsonProperty(value = "sqlQuery")
    private Option<String> sqlQuery;

    /*
     * A database name in this Azure CosmosDB
     */
    @JsonProperty(value = "database")
    private Option<String> database;

    /*
     * A collection id in this database
     */
    @JsonProperty(value = "collectionId")
    private Option<String> collectionId;

    /**
     * Set the connectionString property: The connection string of this Azure CosmosDB.
     *
     * @param connectionString the connectionString value to set.
     * @return the AzureCosmosDBParameterPatch object itself.
     */
    public AzureCosmosDbParameterPatch setConnectionString(String connectionString) {
        if (connectionString == null) {
            this.connectionString = Option.empty();
        } else {
            this.connectionString = Option.of(connectionString);
        }
        return this;
    }

    /**
     * Set the sqlQuery property: The statement to query this collection.
     *
     * @param sqlQuery the sqlQuery value to set.
     * @return the AzureCosmosDBParameterPatch object itself.
     */
    public AzureCosmosDbParameterPatch setSqlQuery(String sqlQuery) {
        if (sqlQuery == null) {
            this.sqlQuery = Option.empty();
        } else {
            this.sqlQuery = Option.of(sqlQuery);
        }
        return this;
    }

    /**
     * Set the database property: A database name in this Azure CosmosDB.
     *
     * @param database the database value to set.
     * @return the AzureCosmosDBParameterPatch object itself.
     */
    public AzureCosmosDbParameterPatch setDatabase(String database) {
        if (database == null) {
            this.database = Option.empty();
        } else {
            this.database = Option.of(database);
        }
        return this;
    }

    /**
     * Set the collectionId property: A collection id in this database.
     *
     * @param collectionId the collectionId value to set.
     * @return the AzureCosmosDBParameterPatch object itself.
     */
    public AzureCosmosDbParameterPatch setCollectionId(String collectionId) {
        if (collectionId == null) {
            this.collectionId = Option.empty();
        } else {
            this.collectionId = Option.of(collectionId);
        }
        return this;
    }
}
