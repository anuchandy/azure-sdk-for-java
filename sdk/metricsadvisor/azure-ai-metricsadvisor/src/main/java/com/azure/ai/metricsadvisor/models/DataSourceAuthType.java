// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.models;

import com.azure.core.util.ExpandableStringEnum;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collection;

/** Defines values for DataSourceAuthType. */
public class DataSourceAuthType extends ExpandableStringEnum<DataSourceAuthType> {
    /** Static value Basic for AuthenticationTypeEnum. */
    public static final DataSourceAuthType BASIC = fromString("Basic");

    /** Static value ManagedIdentity for AuthenticationTypeEnum. */
    public static final DataSourceAuthType MANAGED_IDENTITY = fromString("ManagedIdentity");

    /** Static value AzureSQLConnectionString for AuthenticationTypeEnum. */
    public static final DataSourceAuthType AZURE_SQL_CONNECTION_STRING = fromString("AzureSQLConnectionString");

    /** Static value DataLakeGen2SharedKey for AuthenticationTypeEnum. */
    public static final DataSourceAuthType DATA_LAKE_GEN2SHARED_KEY = fromString("DataLakeGen2SharedKey");

    /** Static value ServicePrincipal for AuthenticationTypeEnum. */
    public static final DataSourceAuthType SERVICE_PRINCIPAL = fromString("ServicePrincipal");

    /** Static value ServicePrincipalInKV for AuthenticationTypeEnum. */
    public static final DataSourceAuthType SERVICE_PRINCIPAL_IN_KV = fromString("ServicePrincipalInKV");

    /**
     * Creates or finds a AuthenticationTypeEnum from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding AuthenticationTypeEnum.
     */
    @JsonCreator
    public static DataSourceAuthType fromString(String name) {
        return fromString(name, DataSourceAuthType.class);
    }

    /** @return known AuthenticationTypeEnum values. */
    public static Collection<DataSourceAuthType> values() {
        return values(DataSourceAuthType.class);
    }
}
