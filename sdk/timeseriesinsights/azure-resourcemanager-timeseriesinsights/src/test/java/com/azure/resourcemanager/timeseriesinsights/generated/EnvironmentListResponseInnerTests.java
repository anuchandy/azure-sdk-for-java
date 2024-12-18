// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.timeseriesinsights.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.timeseriesinsights.fluent.models.EnvironmentListResponseInner;
import com.azure.resourcemanager.timeseriesinsights.fluent.models.EnvironmentResourceInner;
import com.azure.resourcemanager.timeseriesinsights.models.Sku;
import com.azure.resourcemanager.timeseriesinsights.models.SkuName;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class EnvironmentListResponseInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        EnvironmentListResponseInner model = BinaryData.fromString(
            "{\"value\":[{\"kind\":\"EnvironmentResource\",\"sku\":{\"name\":\"S2\",\"capacity\":1448961060},\"location\":\"kyfi\",\"tags\":{\"zwdzuh\":\"idf\",\"wxmnteiwao\":\"ymwisdkft\"},\"id\":\"vkmijcmmxdcuf\",\"name\":\"fsrpymzidnse\",\"type\":\"cxtbzsg\"},{\"kind\":\"EnvironmentResource\",\"sku\":{\"name\":\"S1\",\"capacity\":121296393},\"location\":\"cs\",\"tags\":{\"flnrosfqpteehzz\":\"mdwzjeiachboo\",\"swjdkirso\":\"ypyqrimzinp\",\"soifiyipjxsqw\":\"dqxhcrmnohjtckwh\",\"bznorcjxvsnby\":\"gr\"},\"id\":\"qabnmoc\",\"name\":\"cyshurzafbljjgp\",\"type\":\"toqcjmklja\"}]}")
            .toObject(EnvironmentListResponseInner.class);
        Assertions.assertEquals("kyfi", model.value().get(0).location());
        Assertions.assertEquals("idf", model.value().get(0).tags().get("zwdzuh"));
        Assertions.assertEquals(SkuName.S2, model.value().get(0).sku().name());
        Assertions.assertEquals(1448961060, model.value().get(0).sku().capacity());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        EnvironmentListResponseInner model = new EnvironmentListResponseInner().withValue(Arrays.asList(
            new EnvironmentResourceInner().withLocation("kyfi")
                .withTags(mapOf("zwdzuh", "idf", "wxmnteiwao", "ymwisdkft"))
                .withSku(new Sku().withName(SkuName.S2).withCapacity(1448961060)),
            new EnvironmentResourceInner().withLocation("cs")
                .withTags(mapOf("flnrosfqpteehzz", "mdwzjeiachboo", "swjdkirso", "ypyqrimzinp", "soifiyipjxsqw",
                    "dqxhcrmnohjtckwh", "bznorcjxvsnby", "gr"))
                .withSku(new Sku().withName(SkuName.S1).withCapacity(121296393))));
        model = BinaryData.fromObject(model).toObject(EnvironmentListResponseInner.class);
        Assertions.assertEquals("kyfi", model.value().get(0).location());
        Assertions.assertEquals("idf", model.value().get(0).tags().get("zwdzuh"));
        Assertions.assertEquals(SkuName.S2, model.value().get(0).sku().name());
        Assertions.assertEquals(1448961060, model.value().get(0).sku().capacity());
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
