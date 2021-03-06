/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.machinelearningservices.v2019_05_01;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A DataLakeAnalytics compute.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "computeType")
@JsonTypeName("DataLakeAnalytics")
public class DataLakeAnalytics extends Compute {
    /**
     * The properties property.
     */
    @JsonProperty(value = "properties")
    private DataLakeAnalyticsProperties properties;

    /**
     * Get the properties value.
     *
     * @return the properties value
     */
    public DataLakeAnalyticsProperties properties() {
        return this.properties;
    }

    /**
     * Set the properties value.
     *
     * @param properties the properties value to set
     * @return the DataLakeAnalytics object itself.
     */
    public DataLakeAnalytics withProperties(DataLakeAnalyticsProperties properties) {
        this.properties = properties;
        return this;
    }

}
