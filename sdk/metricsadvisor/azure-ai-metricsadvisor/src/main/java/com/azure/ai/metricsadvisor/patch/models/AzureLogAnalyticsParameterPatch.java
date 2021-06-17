// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The AzureLogAnalyticsParameterPatch model. */
@Fluent
public final class AzureLogAnalyticsParameterPatch {
    /*
     * The tenant id of service principal that have access to this Log
     * Analytics
     */
    @JsonProperty(value = "tenantId")
    private Option<String> tenantId;

    /*
     * The client id of service principal that have access to this Log
     * Analytics
     */
    @JsonProperty(value = "clientId")
    private Option<String> clientId;

    /*
     * The client secret of service principal that have access to this Log
     * Analytics
     */
    @JsonProperty(value = "clientSecret")
    private Option<String> clientSecret;

    /*
     * The workspace id of this Log Analytics
     */
    @JsonProperty(value = "workspaceId")
    private Option<String> workspaceId;

    /*
     * The KQL (Kusto Query Language) query to fetch data from this Log
     * Analytics
     */
    @JsonProperty(value = "query")
    private Option<String> query;

    /**
     * Set the tenantId property: The tenant id of service principal that have access to this Log Analytics.
     *
     * @param tenantId the tenantId value to set.
     * @return the AzureLogAnalyticsParameterPatch object itself.
     */
    public AzureLogAnalyticsParameterPatch setTenantId(String tenantId) {
        if (tenantId == null) {
            this.tenantId = Option.empty();
        } else {
            this.tenantId = Option.of(tenantId);
        }
        return this;
    }

    /**
     * Set the clientId property: The client id of service principal that have access to this Log Analytics.
     *
     * @param clientId the clientId value to set.
     * @return the AzureLogAnalyticsParameterPatch object itself.
     */
    public AzureLogAnalyticsParameterPatch setClientId(String clientId) {
        if (clientId == null) {
            this.clientId = Option.empty();
        } else {
            this.clientId = Option.of(clientId);
        }
        return this;
    }

    /**
     * Set the clientSecret property: The client secret of service principal that have access to this Log Analytics.
     *
     * @param clientSecret the clientSecret value to set.
     * @return the AzureLogAnalyticsParameterPatch object itself.
     */
    public AzureLogAnalyticsParameterPatch setClientSecret(String clientSecret) {
        if (clientSecret == null) {
            this.clientSecret = Option.empty();
        } else {
            this.clientSecret = Option.of(clientSecret);
        }
        return this;
    }

    /**
     * Set the workspaceId property: The workspace id of this Log Analytics.
     *
     * @param workspaceId the workspaceId value to set.
     * @return the AzureLogAnalyticsParameterPatch object itself.
     */
    public AzureLogAnalyticsParameterPatch setWorkspaceId(String workspaceId) {
        if (workspaceId == null) {
            this.workspaceId = Option.empty();
        } else {
            this.workspaceId = Option.of(workspaceId);
        }
        return this;
    }

    /**
     * Set the query property: The KQL (Kusto Query Language) query to fetch data from this Log Analytics.
     *
     * @param query the query value to set.
     * @return the AzureLogAnalyticsParameterPatch object itself.
     */
    public AzureLogAnalyticsParameterPatch setQuery(String query) {
        if (query == null) {
            this.query = Option.empty();
        } else {
            this.query = Option.of(query);
        }
        return this;
    }
}
