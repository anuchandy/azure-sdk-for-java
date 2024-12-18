// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.devhub.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.devhub.fluent.models.DeleteWorkflowResponseInner;
import org.junit.jupiter.api.Assertions;

public final class DeleteWorkflowResponseInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        DeleteWorkflowResponseInner model
            = BinaryData.fromString("{\"status\":\"thz\"}").toObject(DeleteWorkflowResponseInner.class);
        Assertions.assertEquals("thz", model.status());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        DeleteWorkflowResponseInner model = new DeleteWorkflowResponseInner().withStatus("thz");
        model = BinaryData.fromObject(model).toObject(DeleteWorkflowResponseInner.class);
        Assertions.assertEquals("thz", model.status());
    }
}
