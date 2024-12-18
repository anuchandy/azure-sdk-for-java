// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.timeseriesinsights.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.timeseriesinsights.TimeSeriesInsightsManager;
import com.azure.resourcemanager.timeseriesinsights.models.EventSourceCreateOrUpdateParameters;
import com.azure.resourcemanager.timeseriesinsights.models.EventSourceResource;
import com.azure.resourcemanager.timeseriesinsights.models.LocalTimestamp;
import com.azure.resourcemanager.timeseriesinsights.models.LocalTimestampFormat;
import com.azure.resourcemanager.timeseriesinsights.models.LocalTimestampTimeZoneOffset;
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

public final class EventSourcesCreateOrUpdateWithResponseMockTests {
    @Test
    public void testCreateOrUpdateWithResponse() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr
            = "{\"kind\":\"EventSourceResource\",\"location\":\"wqytjrybnwjewgdr\",\"tags\":{\"ifthnz\":\"vnaenqpehindoyg\",\"vhqlkthumaqo\":\"ndslgnayqigynduh\",\"aolps\":\"bgycduiertgccym\"},\"id\":\"lqlfm\",\"name\":\"dnbbglzps\",\"type\":\"iydmcwyhzdxs\"}";

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

        TimeSeriesInsightsManager manager = TimeSeriesInsightsManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        EventSourceResource response = manager.eventSources()
            .createOrUpdateWithResponse("ytkblmpew", "wfbkrvrns", "shqjohxcrsbf",
                new EventSourceCreateOrUpdateParameters().withLocation("bcgjbirxbp")
                    .withTags(mapOf("twss", "rfbjf", "tpvjzbexilzznfqq", "t", "taruoujmkcj", "vwpm"))
                    .withLocalTimestamp(new LocalTimestamp().withFormat(LocalTimestampFormat.EMBEDDED)
                        .withTimeZoneOffset(new LocalTimestampTimeZoneOffset().withPropertyName("uvwbhsqfs"))),
                com.azure.core.util.Context.NONE)
            .getValue();

        Assertions.assertEquals("wqytjrybnwjewgdr", response.location());
        Assertions.assertEquals("vnaenqpehindoyg", response.tags().get("ifthnz"));
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
