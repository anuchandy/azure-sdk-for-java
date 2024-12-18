// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.models.LinkedIntegrationRuntimeType;

public final class LinkedIntegrationRuntimeTypeTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        LinkedIntegrationRuntimeType model
            = BinaryData.fromString("{\"authorizationType\":\"LinkedIntegrationRuntimeType\"}")
                .toObject(LinkedIntegrationRuntimeType.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        LinkedIntegrationRuntimeType model = new LinkedIntegrationRuntimeType();
        model = BinaryData.fromObject(model).toObject(LinkedIntegrationRuntimeType.class);
    }
}
