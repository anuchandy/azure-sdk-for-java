// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.fluent.models.PrivateEndpointConnectionProperties;
import com.azure.resourcemanager.synapse.models.PrivateEndpoint;
import com.azure.resourcemanager.synapse.models.PrivateEndpointConnectionForPrivateLinkHubBasicAutoGenerated;
import com.azure.resourcemanager.synapse.models.PrivateLinkServiceConnectionState;
import org.junit.jupiter.api.Assertions;

public final class PrivateEndpointConnectionForPrivateLinkHubBasicAutoGeneratedTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        PrivateEndpointConnectionForPrivateLinkHubBasicAutoGenerated model = BinaryData.fromString(
            "{\"id\":\"wankixzbi\",\"properties\":{\"privateEndpoint\":{\"id\":\"uttmrywnuzoqft\"},\"privateLinkServiceConnectionState\":{\"status\":\"zrnkcqvyxlwh\",\"description\":\"sicohoqqnwvlry\",\"actionsRequired\":\"w\"},\"provisioningState\":\"eun\"}}")
            .toObject(PrivateEndpointConnectionForPrivateLinkHubBasicAutoGenerated.class);
        Assertions.assertEquals("wankixzbi", model.id());
        Assertions.assertEquals("zrnkcqvyxlwh", model.properties().privateLinkServiceConnectionState().status());
        Assertions.assertEquals("sicohoqqnwvlry", model.properties().privateLinkServiceConnectionState().description());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        PrivateEndpointConnectionForPrivateLinkHubBasicAutoGenerated model
            = new PrivateEndpointConnectionForPrivateLinkHubBasicAutoGenerated().withId("wankixzbi")
                .withProperties(new PrivateEndpointConnectionProperties().withPrivateEndpoint(new PrivateEndpoint())
                    .withPrivateLinkServiceConnectionState(
                        new PrivateLinkServiceConnectionState().withStatus("zrnkcqvyxlwh")
                            .withDescription("sicohoqqnwvlry")));
        model
            = BinaryData.fromObject(model).toObject(PrivateEndpointConnectionForPrivateLinkHubBasicAutoGenerated.class);
        Assertions.assertEquals("wankixzbi", model.id());
        Assertions.assertEquals("zrnkcqvyxlwh", model.properties().privateLinkServiceConnectionState().status());
        Assertions.assertEquals("sicohoqqnwvlry", model.properties().privateLinkServiceConnectionState().description());
    }
}
