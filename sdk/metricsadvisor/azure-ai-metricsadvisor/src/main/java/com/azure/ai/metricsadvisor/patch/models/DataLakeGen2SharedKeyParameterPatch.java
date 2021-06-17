// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The DataLakeGen2SharedKeyParamPatch model. */
@Fluent
public final class DataLakeGen2SharedKeyParameterPatch {
    /*
     * The account key to access the Azure Data Lake Storage Gen2.
     */
    @JsonProperty(value = "accountKey")
    private Option<String> accountKey;

    /**
     * Set the accountKey property: The account key to access the Azure Data Lake Storage Gen2.
     *
     * @param accountKey the accountKey value to set.
     * @return the DataLakeGen2SharedKeyParamPatch object itself.
     */
    public DataLakeGen2SharedKeyParameterPatch setAccountKey(String accountKey) {
        if (accountKey == null) {
            this.accountKey = Option.empty();
        } else {
            this.accountKey = Option.of(accountKey);
        }
        return this;
    }
}
