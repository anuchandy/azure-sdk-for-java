// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.models.SsisEnvironment;
import com.azure.resourcemanager.synapse.models.SsisVariable;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class SsisEnvironmentTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        SsisEnvironment model = BinaryData.fromString(
            "{\"type\":\"Environment\",\"folderId\":293197962792845943,\"variables\":[{\"id\":1447837022418760517,\"name\":\"bgvopemt\",\"description\":\"qujlyegqa\",\"dataType\":\"igflqqbtnyjp\",\"sensitive\":false,\"value\":\"bf\",\"sensitiveValue\":\"bmvmsxba\"}],\"id\":484983659590178245,\"name\":\"nkottlwuhv\",\"description\":\"mailfemjj\"}")
            .toObject(SsisEnvironment.class);
        Assertions.assertEquals(484983659590178245L, model.id());
        Assertions.assertEquals("nkottlwuhv", model.name());
        Assertions.assertEquals("mailfemjj", model.description());
        Assertions.assertEquals(293197962792845943L, model.folderId());
        Assertions.assertEquals(1447837022418760517L, model.variables().get(0).id());
        Assertions.assertEquals("bgvopemt", model.variables().get(0).name());
        Assertions.assertEquals("qujlyegqa", model.variables().get(0).description());
        Assertions.assertEquals("igflqqbtnyjp", model.variables().get(0).dataType());
        Assertions.assertEquals(false, model.variables().get(0).sensitive());
        Assertions.assertEquals("bf", model.variables().get(0).value());
        Assertions.assertEquals("bmvmsxba", model.variables().get(0).sensitiveValue());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        SsisEnvironment model = new SsisEnvironment().withId(484983659590178245L)
            .withName("nkottlwuhv")
            .withDescription("mailfemjj")
            .withFolderId(293197962792845943L)
            .withVariables(Arrays.asList(new SsisVariable().withId(1447837022418760517L)
                .withName("bgvopemt")
                .withDescription("qujlyegqa")
                .withDataType("igflqqbtnyjp")
                .withSensitive(false)
                .withValue("bf")
                .withSensitiveValue("bmvmsxba")));
        model = BinaryData.fromObject(model).toObject(SsisEnvironment.class);
        Assertions.assertEquals(484983659590178245L, model.id());
        Assertions.assertEquals("nkottlwuhv", model.name());
        Assertions.assertEquals("mailfemjj", model.description());
        Assertions.assertEquals(293197962792845943L, model.folderId());
        Assertions.assertEquals(1447837022418760517L, model.variables().get(0).id());
        Assertions.assertEquals("bgvopemt", model.variables().get(0).name());
        Assertions.assertEquals("qujlyegqa", model.variables().get(0).description());
        Assertions.assertEquals("igflqqbtnyjp", model.variables().get(0).dataType());
        Assertions.assertEquals(false, model.variables().get(0).sensitive());
        Assertions.assertEquals("bf", model.variables().get(0).value());
        Assertions.assertEquals("bmvmsxba", model.variables().get(0).sensitiveValue());
    }
}
