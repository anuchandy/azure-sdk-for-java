// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.fluent.models.OperationInner;
import com.azure.resourcemanager.synapse.models.OperationDisplay;
import org.junit.jupiter.api.Assertions;

public final class OperationInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        OperationInner model = BinaryData.fromString(
            "{\"name\":\"cjimryvwgcwwpbmz\",\"display\":{\"provider\":\"sydsxwefohe\",\"operation\":\"vopwndyqleallk\",\"resource\":\"tkhlowkxxpvbr\",\"description\":\"jmzsyzfh\"},\"origin\":\"lhikcyychunsj\",\"properties\":\"datajrtwsz\"}")
            .toObject(OperationInner.class);
        Assertions.assertEquals("cjimryvwgcwwpbmz", model.name());
        Assertions.assertEquals("sydsxwefohe", model.display().provider());
        Assertions.assertEquals("vopwndyqleallk", model.display().operation());
        Assertions.assertEquals("tkhlowkxxpvbr", model.display().resource());
        Assertions.assertEquals("jmzsyzfh", model.display().description());
        Assertions.assertEquals("lhikcyychunsj", model.origin());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        OperationInner model = new OperationInner().withName("cjimryvwgcwwpbmz")
            .withDisplay(new OperationDisplay().withProvider("sydsxwefohe")
                .withOperation("vopwndyqleallk")
                .withResource("tkhlowkxxpvbr")
                .withDescription("jmzsyzfh"))
            .withOrigin("lhikcyychunsj")
            .withProperties("datajrtwsz");
        model = BinaryData.fromObject(model).toObject(OperationInner.class);
        Assertions.assertEquals("cjimryvwgcwwpbmz", model.name());
        Assertions.assertEquals("sydsxwefohe", model.display().provider());
        Assertions.assertEquals("vopwndyqleallk", model.display().operation());
        Assertions.assertEquals("tkhlowkxxpvbr", model.display().resource());
        Assertions.assertEquals("jmzsyzfh", model.display().description());
        Assertions.assertEquals("lhikcyychunsj", model.origin());
    }
}
