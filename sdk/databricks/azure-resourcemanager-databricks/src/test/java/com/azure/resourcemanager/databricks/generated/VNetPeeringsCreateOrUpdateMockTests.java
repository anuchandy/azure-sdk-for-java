// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.databricks.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.resourcemanager.databricks.AzureDatabricksManager;
import com.azure.resourcemanager.databricks.models.AddressSpace;
import com.azure.resourcemanager.databricks.models.VirtualNetworkPeering;
import com.azure.resourcemanager.databricks.models.VirtualNetworkPeeringPropertiesFormatDatabricksVirtualNetwork;
import com.azure.resourcemanager.databricks.models.VirtualNetworkPeeringPropertiesFormatRemoteVirtualNetwork;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class VNetPeeringsCreateOrUpdateMockTests {
    @Test
    public void testCreateOrUpdate() throws Exception {
        String responseStr
            = "{\"properties\":{\"allowVirtualNetworkAccess\":true,\"allowForwardedTraffic\":false,\"allowGatewayTransit\":true,\"useRemoteGateways\":false,\"databricksVirtualNetwork\":{\"id\":\"efyw\"},\"databricksAddressSpace\":{\"addressPrefixes\":[\"vmwy\",\"rfouyftaakcpw\"]},\"remoteVirtualNetwork\":{\"id\":\"zvqtmnubexkp\"},\"remoteAddressSpace\":{\"addressPrefixes\":[\"ondjmq\"]},\"peeringState\":\"Connected\",\"provisioningState\":\"Succeeded\"},\"id\":\"omgkopkwho\",\"name\":\"v\",\"type\":\"ajqgxy\"}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        AzureDatabricksManager manager = AzureDatabricksManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        VirtualNetworkPeering response = manager.vNetPeerings()
            .define("kfvhqcrailvpn")
            .withExistingWorkspace("wdsjnkalju", "iiswacffgdkzze")
            .withRemoteVirtualNetwork(
                new VirtualNetworkPeeringPropertiesFormatRemoteVirtualNetwork().withId("cvdrhvoodsot"))
            .withAllowVirtualNetworkAccess(true)
            .withAllowForwardedTraffic(false)
            .withAllowGatewayTransit(true)
            .withUseRemoteGateways(true)
            .withDatabricksVirtualNetwork(
                new VirtualNetworkPeeringPropertiesFormatDatabricksVirtualNetwork().withId("dlxyjrxs"))
            .withDatabricksAddressSpace(
                new AddressSpace().withAddressPrefixes(Arrays.asList("cnihgwqapnedgfbc", "kcvqvpke")))
            .withRemoteAddressSpace(
                new AddressSpace().withAddressPrefixes(Arrays.asList("dopcjwvnh", "ld", "mgxcxrslpm")))
            .create();

        Assertions.assertEquals(true, response.allowVirtualNetworkAccess());
        Assertions.assertEquals(false, response.allowForwardedTraffic());
        Assertions.assertEquals(true, response.allowGatewayTransit());
        Assertions.assertEquals(false, response.useRemoteGateways());
        Assertions.assertEquals("efyw", response.databricksVirtualNetwork().id());
        Assertions.assertEquals("vmwy", response.databricksAddressSpace().addressPrefixes().get(0));
        Assertions.assertEquals("zvqtmnubexkp", response.remoteVirtualNetwork().id());
        Assertions.assertEquals("ondjmq", response.remoteAddressSpace().addressPrefixes().get(0));
    }
}
