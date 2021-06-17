// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** The ServicePrincipalCredentialPatch model. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataSourceCredentialType")
@JsonTypeName("ServicePrincipal")
@Fluent
public final class DatasourceServicePrincipalPatch extends DatasourceCredentialEntityPatch {
    /*
     * The parameters property.
     */
    @JsonProperty(value = "parameters")
    private Option<ServicePrincipalParameterPatch> parameters;

    /**
     * Set the parameters property: The parameters property.
     *
     * @param parameters the parameters value to set.
     * @return the ServicePrincipalCredentialPatch object itself.
     */
    public DatasourceServicePrincipalPatch setParameters(ServicePrincipalParameterPatch parameters) {
        if (parameters == null) {
            this.parameters = Option.empty();
        } else {
            this.parameters = Option.of(parameters);
        }
        return this;
    }
}
