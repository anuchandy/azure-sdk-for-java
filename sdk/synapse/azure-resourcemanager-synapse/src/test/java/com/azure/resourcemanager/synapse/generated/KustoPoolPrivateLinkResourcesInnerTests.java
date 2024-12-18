// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.fluent.models.KustoPoolPrivateLinkResourcesInner;

public final class KustoPoolPrivateLinkResourcesInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        KustoPoolPrivateLinkResourcesInner model = BinaryData.fromString(
            "{\"properties\":{\"groupId\":\"mxhzzysevus\",\"requiredMembers\":[\"zrrryv\",\"imipskdyzatvfuz\",\"aftjvvruxwigsy\"],\"requiredZoneNames\":[\"qdsmjtg\"],\"provisioningState\":\"Moving\"},\"id\":\"gkkileplkcsmkn\",\"name\":\"wtbbaedorvvmqf\",\"type\":\"oygbdgwumgxd\"}")
            .toObject(KustoPoolPrivateLinkResourcesInner.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        KustoPoolPrivateLinkResourcesInner model = new KustoPoolPrivateLinkResourcesInner();
        model = BinaryData.fromObject(model).toObject(KustoPoolPrivateLinkResourcesInner.class);
    }
}
