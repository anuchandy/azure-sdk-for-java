// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.dashboard.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.dashboard.DashboardManager;
import com.azure.resourcemanager.dashboard.models.ManagedPrivateEndpointModel;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class ManagedPrivateEndpointsListMockTests {
    @Test
    public void testList() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr
            = "{\"value\":[{\"properties\":{\"provisioningState\":\"Deleting\",\"privateLinkResourceId\":\"fssxqukkfplg\",\"privateLinkResourceRegion\":\"sxnkjzkdeslpvlo\",\"groupIds\":[\"yighxpk\"],\"requestMessage\":\"zb\",\"connectionState\":{\"status\":\"Pending\",\"description\":\"baumnyqupedeoj\"},\"privateLinkServiceUrl\":\"bckhsmtxpsi\",\"privateLinkServicePrivateIP\":\"tfhvpesapskrdqmh\"},\"location\":\"dhtldwkyz\",\"tags\":{\"svlxotogtwrup\":\"tkncwsc\",\"nmic\":\"sx\"},\"id\":\"kvceoveilovnotyf\",\"name\":\"fcnj\",\"type\":\"k\"}]}";

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

        DashboardManager manager = DashboardManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        PagedIterable<ManagedPrivateEndpointModel> response
            = manager.managedPrivateEndpoints().list("bhvgy", "gu", com.azure.core.util.Context.NONE);

        Assertions.assertEquals("dhtldwkyz", response.iterator().next().location());
        Assertions.assertEquals("tkncwsc", response.iterator().next().tags().get("svlxotogtwrup"));
        Assertions.assertEquals("fssxqukkfplg", response.iterator().next().privateLinkResourceId());
        Assertions.assertEquals("sxnkjzkdeslpvlo", response.iterator().next().privateLinkResourceRegion());
        Assertions.assertEquals("yighxpk", response.iterator().next().groupIds().get(0));
        Assertions.assertEquals("zb", response.iterator().next().requestMessage());
        Assertions.assertEquals("bckhsmtxpsi", response.iterator().next().privateLinkServiceUrl());
    }
}
