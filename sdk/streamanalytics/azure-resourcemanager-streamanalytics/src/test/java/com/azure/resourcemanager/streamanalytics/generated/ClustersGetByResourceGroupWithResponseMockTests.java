// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.streamanalytics.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.streamanalytics.StreamAnalyticsManager;
import com.azure.resourcemanager.streamanalytics.models.Cluster;
import com.azure.resourcemanager.streamanalytics.models.ClusterSkuName;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class ClustersGetByResourceGroupWithResponseMockTests {
    @Test
    public void testGetByResourceGroupWithResponse() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr
            = "{\"sku\":{\"name\":\"Default\",\"capacity\":1385368954},\"etag\":\"fbylyrfg\",\"properties\":{\"createdDate\":\"2021-11-26T11:51:30Z\",\"clusterId\":\"ojocqwogf\",\"provisioningState\":\"InProgress\",\"capacityAllocated\":2065861659,\"capacityAssigned\":2066687517},\"location\":\"zldmozuxy\",\"tags\":{\"grjqctojcmi\":\"btkadpysownbtgkb\",\"eypefojyqd\":\"of\",\"hlhzdsqtzbsrgno\":\"cuplcplcwkhih\",\"teyowclu\":\"cjhfgmvecactxmw\"},\"id\":\"ovekqvgqouwi\",\"name\":\"zmpjwyiv\",\"type\":\"ikf\"}";

        Mockito.when(httpResponse.getStatusCode()).thenReturn(200);
        Mockito.when(httpResponse.getHeaders()).thenReturn(new HttpHeaders());
        Mockito.when(httpResponse.getBody())
            .thenReturn(Flux.just(ByteBuffer.wrap(responseStr.getBytes(StandardCharsets.UTF_8))));
        Mockito.when(httpResponse.getBodyAsByteArray())
            .thenReturn(Mono.just(responseStr.getBytes(StandardCharsets.UTF_8)));
        Mockito.when(httpClient.send(httpRequest.capture(), Mockito.any())).thenReturn(Mono.defer(() -> {
            Mockito.when(httpResponse.getRequest()).thenReturn(httpRequest.getValue());
            return Mono.just(httpResponse);
        }));

        StreamAnalyticsManager manager = StreamAnalyticsManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        Cluster response = manager.clusters()
            .getByResourceGroupWithResponse("xdbeesmieknl", "ariaawi", com.azure.core.util.Context.NONE)
            .getValue();

        Assertions.assertEquals("zldmozuxy", response.location());
        Assertions.assertEquals("btkadpysownbtgkb", response.tags().get("grjqctojcmi"));
        Assertions.assertEquals(ClusterSkuName.DEFAULT, response.sku().name());
        Assertions.assertEquals(1385368954, response.sku().capacity());
    }
}
