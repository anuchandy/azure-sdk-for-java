// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.streamanalytics.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.streamanalytics.models.AzureMachineLearningServiceInputColumn;
import org.junit.jupiter.api.Assertions;

public final class AzureMachineLearningServiceInputColumnTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        AzureMachineLearningServiceInputColumn model = BinaryData
            .fromString("{\"name\":\"zdzucerscdntnevf\",\"dataType\":\"jmygtdsslswtmwer\",\"mapTo\":539976297}")
            .toObject(AzureMachineLearningServiceInputColumn.class);
        Assertions.assertEquals("zdzucerscdntnevf", model.name());
        Assertions.assertEquals("jmygtdsslswtmwer", model.dataType());
        Assertions.assertEquals(539976297, model.mapTo());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        AzureMachineLearningServiceInputColumn model
            = new AzureMachineLearningServiceInputColumn().withName("zdzucerscdntnevf")
                .withDataType("jmygtdsslswtmwer")
                .withMapTo(539976297);
        model = BinaryData.fromObject(model).toObject(AzureMachineLearningServiceInputColumn.class);
        Assertions.assertEquals("zdzucerscdntnevf", model.name());
        Assertions.assertEquals("jmygtdsslswtmwer", model.dataType());
        Assertions.assertEquals(539976297, model.mapTo());
    }
}
