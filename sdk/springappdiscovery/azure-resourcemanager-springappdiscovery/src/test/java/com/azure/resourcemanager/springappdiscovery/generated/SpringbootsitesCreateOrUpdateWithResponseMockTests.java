// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.springappdiscovery.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.springappdiscovery.SpringAppDiscoveryManager;
import com.azure.resourcemanager.springappdiscovery.models.ProvisioningState;
import com.azure.resourcemanager.springappdiscovery.models.SpringbootsitesModel;
import com.azure.resourcemanager.springappdiscovery.models.SpringbootsitesModelExtendedLocation;
import com.azure.resourcemanager.springappdiscovery.models.SpringbootsitesProperties;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class SpringbootsitesCreateOrUpdateWithResponseMockTests {
    @Test
    public void testCreateOrUpdateWithResponse() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr
            = "{\"properties\":{\"masterSiteId\":\"nvowgujju\",\"migrateProjectId\":\"dkcglhsl\",\"provisioningState\":\"Unknown\"},\"extendedLocation\":{\"type\":\"ggd\",\"name\":\"ixhbkuofqweykhm\"},\"location\":\"evfyexfwhybcib\",\"tags\":{\"ynnaam\":\"dcsi\",\"qsc\":\"ectehf\",\"hcjrefovgmk\":\"eypvhezrkg\"},\"id\":\"sle\",\"name\":\"yvxyqjp\",\"type\":\"cattpngjcrcczsq\"}";

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

        SpringbootsitesModel response = manager.springbootsites()
            .define("isnjampmngnz")
            .withRegion("lwtgrhpdj")
            .withExistingResourceGroup("feallnwsu")
            .withTags(
                mapOf("lhbxxhejjzzvdud", "masxazjpqyegu", "pwlbjnpg", "wdslfhotwmcy", "nltyfsoppusuesnz", "cftadeh"))
            .withProperties(new SpringbootsitesProperties().withMasterSiteId("aqw")
                .withMigrateProjectId("chcbonqvpkvlrxnj")
                .withProvisioningState(ProvisioningState.DELETING))
            .withExtendedLocation(new SpringbootsitesModelExtendedLocation().withType("pheoflokeyy").withName("nj"))
            .create();

        Assertions.assertEquals("evfyexfwhybcib", response.location());
        Assertions.assertEquals("dcsi", response.tags().get("ynnaam"));
        Assertions.assertEquals("nvowgujju", response.properties().masterSiteId());
        Assertions.assertEquals("dkcglhsl", response.properties().migrateProjectId());
        Assertions.assertEquals(ProvisioningState.UNKNOWN, response.properties().provisioningState());
        Assertions.assertEquals("ggd", response.extendedLocation().type());
        Assertions.assertEquals("ixhbkuofqweykhm", response.extendedLocation().name());
    }

    // Use "Map.of" if available
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
