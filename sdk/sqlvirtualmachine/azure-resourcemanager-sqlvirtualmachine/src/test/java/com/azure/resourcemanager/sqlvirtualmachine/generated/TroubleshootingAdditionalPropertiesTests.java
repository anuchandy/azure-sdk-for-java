// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.sqlvirtualmachine.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.sqlvirtualmachine.models.TroubleshootingAdditionalProperties;
import com.azure.resourcemanager.sqlvirtualmachine.models.UnhealthyReplicaInfo;
import org.junit.jupiter.api.Assertions;

public final class TroubleshootingAdditionalPropertiesTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        TroubleshootingAdditionalProperties model
            = BinaryData.fromString("{\"unhealthyReplicaInfo\":{\"availabilityGroupName\":\"pxjmflbvvnchr\"}}")
                .toObject(TroubleshootingAdditionalProperties.class);
        Assertions.assertEquals("pxjmflbvvnchr", model.unhealthyReplicaInfo().availabilityGroupName());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        TroubleshootingAdditionalProperties model = new TroubleshootingAdditionalProperties()
            .withUnhealthyReplicaInfo(new UnhealthyReplicaInfo().withAvailabilityGroupName("pxjmflbvvnchr"));
        model = BinaryData.fromObject(model).toObject(TroubleshootingAdditionalProperties.class);
        Assertions.assertEquals("pxjmflbvvnchr", model.unhealthyReplicaInfo().availabilityGroupName());
    }
}
