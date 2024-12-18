// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.resourcegraph.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.resourcegraph.models.OperationDisplay;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class OperationDisplayTests {
    @Test
    public void testDeserialize() {
        OperationDisplay model = BinaryData
            .fromString(
                "{\"provider\":\"nchgej\",\"resource\":\"odmailzyd\",\"operation\":\"o\",\"description\":\"yahux\"}")
            .toObject(OperationDisplay.class);
        Assertions.assertEquals("nchgej", model.provider());
        Assertions.assertEquals("odmailzyd", model.resource());
        Assertions.assertEquals("o", model.operation());
        Assertions.assertEquals("yahux", model.description());
    }

    @Test
    public void testSerialize() {
        OperationDisplay model = new OperationDisplay().withProvider("nchgej")
            .withResource("odmailzyd")
            .withOperation("o")
            .withDescription("yahux");
        model = BinaryData.fromObject(model).toObject(OperationDisplay.class);
        Assertions.assertEquals("nchgej", model.provider());
        Assertions.assertEquals("odmailzyd", model.resource());
        Assertions.assertEquals("o", model.operation());
        Assertions.assertEquals("yahux", model.description());
    }
}
