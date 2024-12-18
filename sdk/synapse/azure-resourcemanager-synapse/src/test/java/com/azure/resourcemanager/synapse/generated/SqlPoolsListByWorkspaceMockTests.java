// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.synapse.SynapseManager;
import com.azure.resourcemanager.synapse.models.CreateMode;
import com.azure.resourcemanager.synapse.models.SqlPool;
import com.azure.resourcemanager.synapse.models.StorageAccountType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class SqlPoolsListByWorkspaceMockTests {
    @Test
    public void testListByWorkspace() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr
            = "{\"value\":[{\"sku\":{\"tier\":\"pvozglqjbknlzc\",\"name\":\"tzeyowmndcovd\",\"capacity\":1784939466},\"properties\":{\"maxSizeBytes\":7398433099501562238,\"collation\":\"nhmkvfruwku\",\"sourceDatabaseId\":\"bcpftxudqyemebun\",\"recoverableDatabaseId\":\"cmcir\",\"provisioningState\":\"eemmjauwcgx\",\"status\":\"noh\",\"restorePointInTime\":\"2021-06-10T07:29:48Z\",\"createMode\":\"Restore\",\"creationDate\":\"2021-08-02T08:56:51Z\",\"storageAccountType\":\"GRS\",\"sourceDatabaseDeletionDate\":\"2021-03-02T16:05:57Z\"},\"location\":\"gudasmxubvfb\",\"tags\":{\"hpriylfm\":\"coce\",\"vhl\":\"ztraud\",\"tmojhvrztnvgyshq\":\"dculregp\"},\"id\":\"dgrtwmew\",\"name\":\"zlpykcfazzwjcay\",\"type\":\"rzrr\"}]}";

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

        SynapseManager manager = SynapseManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        PagedIterable<SqlPool> response
            = manager.sqlPools().listByWorkspace("ewfopazdazg", "sqgpewqcfu", com.azure.core.util.Context.NONE);

        Assertions.assertEquals("gudasmxubvfb", response.iterator().next().location());
        Assertions.assertEquals("coce", response.iterator().next().tags().get("hpriylfm"));
        Assertions.assertEquals("pvozglqjbknlzc", response.iterator().next().sku().tier());
        Assertions.assertEquals("tzeyowmndcovd", response.iterator().next().sku().name());
        Assertions.assertEquals(1784939466, response.iterator().next().sku().capacity());
        Assertions.assertEquals(7398433099501562238L, response.iterator().next().maxSizeBytes());
        Assertions.assertEquals("nhmkvfruwku", response.iterator().next().collation());
        Assertions.assertEquals("bcpftxudqyemebun", response.iterator().next().sourceDatabaseId());
        Assertions.assertEquals("cmcir", response.iterator().next().recoverableDatabaseId());
        Assertions.assertEquals("eemmjauwcgx", response.iterator().next().provisioningState());
        Assertions.assertEquals(OffsetDateTime.parse("2021-06-10T07:29:48Z"),
            response.iterator().next().restorePointInTime());
        Assertions.assertEquals(CreateMode.RESTORE, response.iterator().next().createMode());
        Assertions.assertEquals(StorageAccountType.GRS, response.iterator().next().storageAccountType());
        Assertions.assertEquals(OffsetDateTime.parse("2021-03-02T16:05:57Z"),
            response.iterator().next().sourceDatabaseDeletionDate());
    }
}
