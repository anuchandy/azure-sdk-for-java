// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** The DataSourceCredentialPatch model. */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dataSourceCredentialType",
        defaultImpl = DatasourceCredentialEntityPatch.class)
@JsonTypeName("DataSourceCredentialPatch")
@JsonSubTypes({
    @JsonSubTypes.Type(name = "AzureSQLConnectionString", value = DatasourceSqlConnectionStringPatch.class),
    @JsonSubTypes.Type(name = "DataLakeGen2SharedKey", value = DatasourceDataLakeGen2SharedKeyPatch.class),
    @JsonSubTypes.Type(name = "ServicePrincipal", value = DatasourceServicePrincipalPatch.class),
    @JsonSubTypes.Type(name = "ServicePrincipalInKV", value = DatasourceServicePrincipalInKeyVaultPatch.class)
})
@Fluent
public class DatasourceCredentialEntityPatch {
    /*
     * Name of data source credential
     */
    @JsonProperty(value = "dataSourceCredentialName")
    private String dataSourceCredentialName;

    /*
     * Description of data source credential
     */
    @JsonProperty(value = "dataSourceCredentialDescription")
    private String dataSourceCredentialDescription;

    /**
     * Set the dataSourceCredentialName property: Name of data source credential.
     *
     * @param name the dataSourceCredentialName value to set.
     * @return the DataSourceCredentialPatch object itself.
     */
    public DatasourceCredentialEntityPatch setName(String name) {
        this.dataSourceCredentialName = name;
        return this;
    }

    /**
     * Set the dataSourceCredentialDescription property: Description of data source credential.
     *
     * @param description the dataSourceCredentialDescription value to set.
     * @return the DataSourceCredentialPatch object itself.
     */
    public DatasourceCredentialEntityPatch setDescription(String description) {
        this.dataSourceCredentialDescription = description;
        return this;
    }
}
