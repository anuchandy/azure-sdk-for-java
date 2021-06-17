// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The InfluxDBParameterPatch model. */
@Fluent
public final class InfluxDbParameterPatch {
    /*
     * The connection string of this InfluxDB
     */
    @JsonProperty(value = "connectionString")
    private Option<String> connectionString;

    /*
     * A database name
     */
    @JsonProperty(value = "database")
    private Option<String> database;

    /*
     * The user name of the account that can access this database
     */
    @JsonProperty(value = "userName")
    private Option<String> userName;

    /*
     * The password of the account that can access this database
     */
    @JsonProperty(value = "password")
    private Option<String> password;

    /*
     * The script to query this database
     */
    @JsonProperty(value = "query")
    private Option<String> query;

    /**
     * Set the connectionString property: The connection string of this InfluxDB.
     *
     * @param connectionString the connectionString value to set.
     * @return the InfluxDBParameterPatch object itself.
     */
    public InfluxDbParameterPatch setConnectionString(String connectionString) {
        if (connectionString == null) {
            this.connectionString = Option.empty();
        } else {
            this.connectionString = Option.of(connectionString);
        }
        return this;
    }

    /**
     * Set the database property: A database name.
     *
     * @param database the database value to set.
     * @return the InfluxDBParameterPatch object itself.
     */
    public InfluxDbParameterPatch setDatabase(String database) {
        if (database == null) {
            this.database = Option.empty();
        } else {
            this.database = Option.of(database);
        }
        return this;
    }

    /**
     * Set the userName property: The user name of the account that can access this database.
     *
     * @param userName the userName value to set.
     * @return the InfluxDBParameterPatch object itself.
     */
    public InfluxDbParameterPatch setUserName(String userName) {
        if (userName == null) {
            this.userName = Option.empty();
        } else {
            this.userName = Option.of(userName);
        }
        return this;
    }

    /**
     * Set the password property: The password of the account that can access this database.
     *
     * @param password the password value to set.
     * @return the InfluxDBParameterPatch object itself.
     */
    public InfluxDbParameterPatch setPassword(String password) {
        if (password == null) {
            this.password = Option.empty();
        } else {
            this.password = Option.of(password);
        }
        return this;
    }

    /**
     * Set the query property: The script to query this database.
     *
     * @param query the query value to set.
     * @return the InfluxDBParameterPatch object itself.
     */
    public InfluxDbParameterPatch setQuery(String query) {
        if (query == null) {
            this.query = Option.empty();
        } else {
            this.query = Option.of(query);
        }
        return this;
    }
}
