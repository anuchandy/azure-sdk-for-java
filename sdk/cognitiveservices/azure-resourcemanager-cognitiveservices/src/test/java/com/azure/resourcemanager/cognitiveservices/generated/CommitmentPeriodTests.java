// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.cognitiveservices.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.cognitiveservices.models.CommitmentPeriod;
import org.junit.jupiter.api.Assertions;

public final class CommitmentPeriodTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        CommitmentPeriod model = BinaryData.fromString(
            "{\"tier\":\"gjqppy\",\"count\":768184156,\"quota\":{\"quantity\":2863905998828514603,\"unit\":\"yhgfipnsx\"},\"startDate\":\"cwaekrrjre\",\"endDate\":\"xt\"}")
            .toObject(CommitmentPeriod.class);
        Assertions.assertEquals("gjqppy", model.tier());
        Assertions.assertEquals(768184156, model.count());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        CommitmentPeriod model = new CommitmentPeriod().withTier("gjqppy").withCount(768184156);
        model = BinaryData.fromObject(model).toObject(CommitmentPeriod.class);
        Assertions.assertEquals("gjqppy", model.tier());
        Assertions.assertEquals(768184156, model.count());
    }
}
