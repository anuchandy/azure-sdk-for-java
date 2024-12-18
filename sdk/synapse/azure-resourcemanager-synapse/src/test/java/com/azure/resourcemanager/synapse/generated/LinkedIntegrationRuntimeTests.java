// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.models.LinkedIntegrationRuntime;

public final class LinkedIntegrationRuntimeTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        LinkedIntegrationRuntime model = BinaryData.fromString(
            "{\"name\":\"s\",\"subscriptionId\":\"k\",\"dataFactoryName\":\"bcufqbvntn\",\"dataFactoryLocation\":\"mqso\",\"createTime\":\"2021-05-17T14:08:07Z\"}")
            .toObject(LinkedIntegrationRuntime.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        LinkedIntegrationRuntime model = new LinkedIntegrationRuntime();
        model = BinaryData.fromObject(model).toObject(LinkedIntegrationRuntime.class);
    }
}
