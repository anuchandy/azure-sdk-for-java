// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.models.SkuLocationInfoItem;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class SkuLocationInfoItemTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        SkuLocationInfoItem model
            = BinaryData.fromString("{\"location\":\"xsmlz\",\"zones\":[\"dtxetlgydlh\",\"vlnnpx\",\"b\"]}")
                .toObject(SkuLocationInfoItem.class);
        Assertions.assertEquals("xsmlz", model.location());
        Assertions.assertEquals("dtxetlgydlh", model.zones().get(0));
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        SkuLocationInfoItem model
            = new SkuLocationInfoItem().withLocation("xsmlz").withZones(Arrays.asList("dtxetlgydlh", "vlnnpx", "b"));
        model = BinaryData.fromObject(model).toObject(SkuLocationInfoItem.class);
        Assertions.assertEquals("xsmlz", model.location());
        Assertions.assertEquals("dtxetlgydlh", model.zones().get(0));
    }
}
