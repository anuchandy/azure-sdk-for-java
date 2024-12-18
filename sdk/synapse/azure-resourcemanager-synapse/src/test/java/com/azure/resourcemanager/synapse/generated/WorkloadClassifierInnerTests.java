// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.fluent.models.WorkloadClassifierInner;
import org.junit.jupiter.api.Assertions;

public final class WorkloadClassifierInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        WorkloadClassifierInner model = BinaryData.fromString(
            "{\"properties\":{\"memberName\":\"vokqdzfv\",\"label\":\"ivjlfrqttbajlka\",\"context\":\"wxyiopidkqq\",\"startTime\":\"uvscxkdmligov\",\"endTime\":\"rxkpmloazuruoc\",\"importance\":\"oorb\"},\"id\":\"eoybfhjxakvvjgs\",\"name\":\"ordilmywwtkgkxny\",\"type\":\"dabg\"}")
            .toObject(WorkloadClassifierInner.class);
        Assertions.assertEquals("vokqdzfv", model.memberName());
        Assertions.assertEquals("ivjlfrqttbajlka", model.label());
        Assertions.assertEquals("wxyiopidkqq", model.context());
        Assertions.assertEquals("uvscxkdmligov", model.startTime());
        Assertions.assertEquals("rxkpmloazuruoc", model.endTime());
        Assertions.assertEquals("oorb", model.importance());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        WorkloadClassifierInner model = new WorkloadClassifierInner().withMemberName("vokqdzfv")
            .withLabel("ivjlfrqttbajlka")
            .withContext("wxyiopidkqq")
            .withStartTime("uvscxkdmligov")
            .withEndTime("rxkpmloazuruoc")
            .withImportance("oorb");
        model = BinaryData.fromObject(model).toObject(WorkloadClassifierInner.class);
        Assertions.assertEquals("vokqdzfv", model.memberName());
        Assertions.assertEquals("ivjlfrqttbajlka", model.label());
        Assertions.assertEquals("wxyiopidkqq", model.context());
        Assertions.assertEquals("uvscxkdmligov", model.startTime());
        Assertions.assertEquals("rxkpmloazuruoc", model.endTime());
        Assertions.assertEquals("oorb", model.importance());
    }
}
