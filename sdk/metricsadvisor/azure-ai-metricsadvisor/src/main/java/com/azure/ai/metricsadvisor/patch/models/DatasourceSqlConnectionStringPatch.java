// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** The AzureSQLConnectionStringCredentialPatch model. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataSourceCredentialType")
@JsonTypeName("AzureSQLConnectionString")
@Fluent
public final class DatasourceSqlConnectionStringPatch extends DatasourceCredentialEntityPatch {
    /*
     * The parameters property.
     */
    @JsonProperty(value = "parameters")
    private Option<AzureSQLConnectionStringParameterPatch> parameters;

    /**
     * Set the parameters property: The parameters property.
     *
     * @param parameters the parameters value to set.
     * @return the AzureSQLConnectionStringCredentialPatch object itself.
     */
    public DatasourceSqlConnectionStringPatch setParameters(AzureSQLConnectionStringParameterPatch parameters) {
        if (parameters == null) {
            this.parameters = Option.empty();
        } else {
            this.parameters = Option.of(parameters);
        }
        return this;
    }
}
