// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.fluent.models.IntegrationRuntimeEnableinteractivequeryInner;
import com.azure.resourcemanager.synapse.models.WorkspaceStatus;
import org.junit.jupiter.api.Assertions;

public final class IntegrationRuntimeEnableinteractivequeryInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        IntegrationRuntimeEnableinteractivequeryInner model = BinaryData
            .fromString(
                "{\"status\":\"Failed\",\"name\":\"bgdlfgtdysna\",\"properties\":\"dataflq\",\"error\":\"tqhamzjrw\"}")
            .toObject(IntegrationRuntimeEnableinteractivequeryInner.class);
        Assertions.assertEquals(WorkspaceStatus.FAILED, model.status());
        Assertions.assertEquals("bgdlfgtdysna", model.name());
        Assertions.assertEquals("tqhamzjrw", model.error());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        IntegrationRuntimeEnableinteractivequeryInner model
            = new IntegrationRuntimeEnableinteractivequeryInner().withStatus(WorkspaceStatus.FAILED)
                .withName("bgdlfgtdysna")
                .withProperties("dataflq")
                .withError("tqhamzjrw");
        model = BinaryData.fromObject(model).toObject(IntegrationRuntimeEnableinteractivequeryInner.class);
        Assertions.assertEquals(WorkspaceStatus.FAILED, model.status());
        Assertions.assertEquals("bgdlfgtdysna", model.name());
        Assertions.assertEquals("tqhamzjrw", model.error());
    }
}
