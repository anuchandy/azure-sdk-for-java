// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.fluent.models.IntegrationRuntimeOutboundNetworkDependenciesEndpointsResponseInner;
import com.azure.resourcemanager.synapse.models.IntegrationRuntimeOutboundNetworkDependenciesCategoryEndpoint;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class IntegrationRuntimeOutboundNetworkDependenciesEndpointsResponseInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        IntegrationRuntimeOutboundNetworkDependenciesEndpointsResponseInner model = BinaryData.fromString(
            "{\"value\":[{\"category\":\"tfjgt\",\"endpoints\":[]},{\"category\":\"vzuyturmlmu\",\"endpoints\":[]},{\"category\":\"bauiropi\",\"endpoints\":[]},{\"category\":\"onwpnga\",\"endpoints\":[]}]}")
            .toObject(IntegrationRuntimeOutboundNetworkDependenciesEndpointsResponseInner.class);
        Assertions.assertEquals("tfjgt", model.value().get(0).category());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        IntegrationRuntimeOutboundNetworkDependenciesEndpointsResponseInner model
            = new IntegrationRuntimeOutboundNetworkDependenciesEndpointsResponseInner().withValue(Arrays.asList(
                new IntegrationRuntimeOutboundNetworkDependenciesCategoryEndpoint().withCategory("tfjgt")
                    .withEndpoints(Arrays.asList()),
                new IntegrationRuntimeOutboundNetworkDependenciesCategoryEndpoint().withCategory("vzuyturmlmu")
                    .withEndpoints(Arrays.asList()),
                new IntegrationRuntimeOutboundNetworkDependenciesCategoryEndpoint().withCategory("bauiropi")
                    .withEndpoints(Arrays.asList()),
                new IntegrationRuntimeOutboundNetworkDependenciesCategoryEndpoint().withCategory("onwpnga")
                    .withEndpoints(Arrays.asList())));
        model = BinaryData.fromObject(model)
            .toObject(IntegrationRuntimeOutboundNetworkDependenciesEndpointsResponseInner.class);
        Assertions.assertEquals("tfjgt", model.value().get(0).category());
    }
}
