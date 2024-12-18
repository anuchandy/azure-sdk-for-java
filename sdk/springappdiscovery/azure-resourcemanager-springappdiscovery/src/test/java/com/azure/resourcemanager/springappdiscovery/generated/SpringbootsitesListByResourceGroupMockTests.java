// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.springappdiscovery.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.springappdiscovery.SpringAppDiscoveryManager;
import com.azure.resourcemanager.springappdiscovery.models.ProvisioningState;
import com.azure.resourcemanager.springappdiscovery.models.SpringbootsitesModel;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class SpringbootsitesListByResourceGroupMockTests {
    @Test
    public void testListByResourceGroup() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr
            = "{\"value\":[{\"properties\":{\"masterSiteId\":\"bqe\",\"migrateProjectId\":\"nxqbzvddn\",\"provisioningState\":\"Succeeded\"},\"extendedLocation\":{\"type\":\"icbtwnpzao\",\"name\":\"uhrhcffcyddgl\"},\"location\":\"t\",\"tags\":{\"yeicxmqciwqvhk\":\"kw\",\"ghmewuam\":\"ixuigdtopbobj\",\"gvdfgiotkftutq\":\"uhrzayvvt\"},\"id\":\"ln\",\"name\":\"xlefgugnxkrx\",\"type\":\"qmi\"}]}";

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

        SpringAppDiscoveryManager manager = SpringAppDiscoveryManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        PagedIterable<SpringbootsitesModel> response
            = manager.springbootsites().listByResourceGroup("hd", com.azure.core.util.Context.NONE);

        Assertions.assertEquals("t", response.iterator().next().location());
        Assertions.assertEquals("kw", response.iterator().next().tags().get("yeicxmqciwqvhk"));
        Assertions.assertEquals("bqe", response.iterator().next().properties().masterSiteId());
        Assertions.assertEquals("nxqbzvddn", response.iterator().next().properties().migrateProjectId());
        Assertions.assertEquals(ProvisioningState.SUCCEEDED,
            response.iterator().next().properties().provisioningState());
        Assertions.assertEquals("icbtwnpzao", response.iterator().next().extendedLocation().type());
        Assertions.assertEquals("uhrhcffcyddgl", response.iterator().next().extendedLocation().name());
    }
}
