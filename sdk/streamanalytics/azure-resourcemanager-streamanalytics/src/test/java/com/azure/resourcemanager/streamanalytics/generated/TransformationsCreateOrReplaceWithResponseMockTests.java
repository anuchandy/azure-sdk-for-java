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
import com.azure.resourcemanager.streamanalytics.models.Transformation;
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

public final class TransformationsCreateOrReplaceWithResponseMockTests {
    @Test
    public void testCreateOrReplaceWithResponse() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr
            = "{\"properties\":{\"streamingUnits\":1700872320,\"validStreamingUnits\":[640278837,1078649712],\"query\":\"srvhmgorffuki\",\"etag\":\"vwmzhwplefaxvxil\"},\"name\":\"tg\",\"type\":\"nzeyqxtjj\",\"id\":\"qlqhycavodg\"}";

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

        Transformation response = manager.transformations()
            .define("vgtrdcnifmzzs")
            .withExistingStreamingjob("hihfrbbcevqagtlt", "hlfkqojpy")
            .withName("fwafqrouda")
            .withStreamingUnits(599707965)
            .withValidStreamingUnits(Arrays.asList(1235749271, 1977061992, 44176896, 1995074524))
            .withQuery("prafwgckhoc")
            .withIfMatch("cdyuibhmfdnbzyd")
            .withIfNoneMatch("f")
            .create();

        Assertions.assertEquals("qlqhycavodg", response.id());
        Assertions.assertEquals("tg", response.name());
        Assertions.assertEquals(1700872320, response.streamingUnits());
        Assertions.assertEquals(640278837, response.validStreamingUnits().get(0));
        Assertions.assertEquals("srvhmgorffuki", response.query());
    }
}
