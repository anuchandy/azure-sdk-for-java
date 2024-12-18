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
import com.azure.resourcemanager.streamanalytics.models.PrivateEndpoint;
import com.azure.resourcemanager.streamanalytics.models.PrivateEndpointProperties;
import com.azure.resourcemanager.streamanalytics.models.PrivateLinkConnectionState;
import com.azure.resourcemanager.streamanalytics.models.PrivateLinkServiceConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class PrivateEndpointsCreateOrUpdateWithResponseMockTests {
    @Test
    public void testCreateOrUpdateWithResponse() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr
            = "{\"properties\":{\"createdDate\":\"kjpdnjzhajo\",\"manualPrivateLinkServiceConnections\":[{\"properties\":{\"privateLinkServiceId\":\"muoyxprimr\",\"groupIds\":[\"teecjmeislst\",\"asylwx\"],\"requestMessage\":\"aumweoohguufu\",\"privateLinkServiceConnectionState\":{}}},{\"properties\":{\"privateLinkServiceId\":\"jathwtzo\",\"groupIds\":[\"emwmdxmebwjs\"],\"requestMessage\":\"p\",\"privateLinkServiceConnectionState\":{}}}]},\"etag\":\"xveabf\",\"id\":\"xnmwmqtibxyijddt\",\"name\":\"qcttadijaeukmrsi\",\"type\":\"ekpndzaapmudq\"}";

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

        PrivateEndpoint response
            = manager.privateEndpoints()
                .define("xulcdisdos")
                .withExistingCluster("gukkjqnvbroy", "a")
                .withProperties(
                    new PrivateEndpointProperties()
                        .withManualPrivateLinkServiceConnections(Arrays.asList(
                            new PrivateLinkServiceConnection().withPrivateLinkServiceId("yvycytdclxgcckn")
                                .withGroupIds(Arrays.asList("mbtmvpdvjdhttzae", "edxihchrphkmcrj", "qnsdfzpbgtgky",
                                    "kdghrjeuutlwx"))
                                .withPrivateLinkServiceConnectionState(new PrivateLinkConnectionState()),
                            new PrivateLinkServiceConnection().withPrivateLinkServiceId("vbwnhhtq")
                                .withGroupIds(Arrays.asList("hgppipifhpfeoa", "vgcxtx"))
                                .withPrivateLinkServiceConnectionState(new PrivateLinkConnectionState()),
                            new PrivateLinkServiceConnection().withPrivateLinkServiceId("sr")
                                .withGroupIds(Arrays.asList("kssjhoiftxfk"))
                                .withPrivateLinkServiceConnectionState(new PrivateLinkConnectionState()))))
                .withIfMatch("qzmiza")
                .withIfNoneMatch("a")
                .create();

        Assertions.assertEquals("muoyxprimr",
            response.properties().manualPrivateLinkServiceConnections().get(0).privateLinkServiceId());
        Assertions.assertEquals("teecjmeislst",
            response.properties().manualPrivateLinkServiceConnections().get(0).groupIds().get(0));
    }
}
