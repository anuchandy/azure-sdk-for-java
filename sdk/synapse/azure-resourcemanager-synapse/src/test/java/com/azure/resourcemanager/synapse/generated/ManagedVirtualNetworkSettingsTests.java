// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.models.ManagedVirtualNetworkSettings;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class ManagedVirtualNetworkSettingsTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ManagedVirtualNetworkSettings model = BinaryData.fromString(
            "{\"preventDataExfiltration\":false,\"linkedAccessCheckOnTargetResource\":true,\"allowedAadTenantIdsForLinking\":[\"pmyyefrpmpdnqq\",\"ka\",\"ao\",\"vmm\"]}")
            .toObject(ManagedVirtualNetworkSettings.class);
        Assertions.assertEquals(false, model.preventDataExfiltration());
        Assertions.assertEquals(true, model.linkedAccessCheckOnTargetResource());
        Assertions.assertEquals("pmyyefrpmpdnqq", model.allowedAadTenantIdsForLinking().get(0));
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ManagedVirtualNetworkSettings model = new ManagedVirtualNetworkSettings().withPreventDataExfiltration(false)
            .withLinkedAccessCheckOnTargetResource(true)
            .withAllowedAadTenantIdsForLinking(Arrays.asList("pmyyefrpmpdnqq", "ka", "ao", "vmm"));
        model = BinaryData.fromObject(model).toObject(ManagedVirtualNetworkSettings.class);
        Assertions.assertEquals(false, model.preventDataExfiltration());
        Assertions.assertEquals(true, model.linkedAccessCheckOnTargetResource());
        Assertions.assertEquals("pmyyefrpmpdnqq", model.allowedAadTenantIdsForLinking().get(0));
    }
}
