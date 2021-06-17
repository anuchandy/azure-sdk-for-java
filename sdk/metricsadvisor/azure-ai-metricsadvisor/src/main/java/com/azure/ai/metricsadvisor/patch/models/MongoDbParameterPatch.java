// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The MongoDBParameterPatch model. */
@Fluent
public final class MongoDbParameterPatch {
    /*
     * The connection string of this MongoDB
     */
    @JsonProperty(value = "connectionString")
    private Option<String> connectionString;

    /*
     * A database name in this MongoDB
     */
    @JsonProperty(value = "database")
    private Option<String> database;

    /*
     * The script to query this database
     */
    @JsonProperty(value = "command")
    private Option<String> command;

    /**
     * Set the connectionString property: The connection string of this MongoDB.
     *
     * @param connectionString the connectionString value to set.
     * @return the MongoDBParameterPatch object itself.
     */
    public MongoDbParameterPatch setConnectionString(String connectionString) {
        if (connectionString == null) {
            this.connectionString = Option.empty();
        } else {
            this.connectionString = Option.of(connectionString);
        }
        return this;
    }

    /**
     * Set the database property: A database name in this MongoDB.
     *
     * @param database the database value to set.
     * @return the MongoDBParameterPatch object itself.
     */
    public MongoDbParameterPatch setDatabase(String database) {
        if (database == null) {
            this.database = Option.empty();
        } else {
            this.database = Option.of(database);
        }
        return this;
    }

    /**
     * Set the command property: The script to query this database.
     *
     * @param command the command value to set.
     * @return the MongoDBParameterPatch object itself.
     */
    public MongoDbParameterPatch setCommand(String command) {
        if (command == null) {
            this.command = Option.empty();
        } else {
            this.command = Option.of(command);
        }
        return this;
    }
}
