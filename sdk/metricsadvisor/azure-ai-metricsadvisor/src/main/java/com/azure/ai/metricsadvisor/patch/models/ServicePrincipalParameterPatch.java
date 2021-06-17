// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The ServicePrincipalParamPatch model. */
@Fluent
public final class ServicePrincipalParameterPatch {
    /*
     * The client id of the service principal.
     */
    @JsonProperty(value = "clientId")
    private Option<String> clientId;

    /*
     * The client secret of the service principal.
     */
    @JsonProperty(value = "clientSecret")
    private Option<String> clientSecret;

    /*
     * The tenant id of the service principal.
     */
    @JsonProperty(value = "tenantId")
    private Option<String> tenantId;

    /**
     * Set the clientId property: The client id of the service principal.
     *
     * @param clientId the clientId value to set.
     * @return the ServicePrincipalParamPatch object itself.
     */
    public ServicePrincipalParameterPatch setClientId(String clientId) {
        if (clientId == null) {
            this.clientId = Option.empty();
        } else {
            this.clientId = Option.of(clientId);
        }
        return this;
    }

    /**
     * Set the clientSecret property: The client secret of the service principal.
     *
     * @param clientSecret the clientSecret value to set.
     * @return the ServicePrincipalParamPatch object itself.
     */
    public ServicePrincipalParameterPatch setClientSecret(String clientSecret) {
        if (clientSecret == null) {
            this.clientSecret = Option.empty();
        } else {
            this.clientSecret = Option.of(clientSecret);
        }
        return this;
    }

    /**
     * Set the tenantId property: The tenant id of the service principal.
     *
     * @param tenantId the tenantId value to set.
     * @return the ServicePrincipalParamPatch object itself.
     */
    public ServicePrincipalParameterPatch setTenantId(String tenantId) {
        if (tenantId == null) {
            this.tenantId = Option.empty();
        } else {
            this.tenantId = Option.of(tenantId);
        }
        return this;
    }
}
