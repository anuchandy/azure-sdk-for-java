// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.networkcloud.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.networkcloud.fluent.models.RackInner;
import com.azure.resourcemanager.networkcloud.models.ExtendedLocation;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class RackInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        RackInner model =
            BinaryData
                .fromString(
                    "{\"extendedLocation\":{\"name\":\"pzlrphw\",\"type\":\"soldweyuqdunv\"},\"properties\":{\"availabilityZone\":\"nnrwrbiork\",\"clusterId\":\"lywjhh\",\"detailedStatus\":\"Error\",\"detailedStatusMessage\":\"xmsivfomiloxggdu\",\"provisioningState\":\"Succeeded\",\"rackLocation\":\"ndieuzaofj\",\"rackSerialNumber\":\"hvcyyysfg\",\"rackSkuId\":\"otcubi\"},\"location\":\"uipwoqonmacje\",\"tags\":{\"cimpevfg\":\"zshq\"},\"id\":\"b\",\"name\":\"rrilbywdxsmic\",\"type\":\"wrwfscjfnyns\"}")
                .toObject(RackInner.class);
        Assertions.assertEquals("uipwoqonmacje", model.location());
        Assertions.assertEquals("zshq", model.tags().get("cimpevfg"));
        Assertions.assertEquals("pzlrphw", model.extendedLocation().name());
        Assertions.assertEquals("soldweyuqdunv", model.extendedLocation().type());
        Assertions.assertEquals("nnrwrbiork", model.availabilityZone());
        Assertions.assertEquals("ndieuzaofj", model.rackLocation());
        Assertions.assertEquals("hvcyyysfg", model.rackSerialNumber());
        Assertions.assertEquals("otcubi", model.rackSkuId());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        RackInner model =
            new RackInner()
                .withLocation("uipwoqonmacje")
                .withTags(mapOf("cimpevfg", "zshq"))
                .withExtendedLocation(new ExtendedLocation().withName("pzlrphw").withType("soldweyuqdunv"))
                .withAvailabilityZone("nnrwrbiork")
                .withRackLocation("ndieuzaofj")
                .withRackSerialNumber("hvcyyysfg")
                .withRackSkuId("otcubi");
        model = BinaryData.fromObject(model).toObject(RackInner.class);
        Assertions.assertEquals("uipwoqonmacje", model.location());
        Assertions.assertEquals("zshq", model.tags().get("cimpevfg"));
        Assertions.assertEquals("pzlrphw", model.extendedLocation().name());
        Assertions.assertEquals("soldweyuqdunv", model.extendedLocation().type());
        Assertions.assertEquals("nnrwrbiork", model.availabilityZone());
        Assertions.assertEquals("ndieuzaofj", model.rackLocation());
        Assertions.assertEquals("hvcyyysfg", model.rackSerialNumber());
        Assertions.assertEquals("otcubi", model.rackSkuId());
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}