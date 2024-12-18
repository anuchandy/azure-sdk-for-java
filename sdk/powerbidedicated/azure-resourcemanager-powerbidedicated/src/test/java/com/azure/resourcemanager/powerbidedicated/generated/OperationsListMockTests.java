// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.powerbidedicated.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.resourcemanager.powerbidedicated.PowerBIDedicatedManager;
import com.azure.resourcemanager.powerbidedicated.models.Operation;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class OperationsListMockTests {
    @Test
    public void testList() throws Exception {
        String responseStr
            = "{\"value\":[{\"name\":\"nnprn\",\"display\":{\"provider\":\"eilpjzuaejxdu\",\"resource\":\"skzbb\",\"operation\":\"zumveekgpwo\",\"description\":\"hkfpbs\"},\"origin\":\"ofd\",\"properties\":{\"serviceSpecification\":{\"metricSpecifications\":[{\"name\":\"ttouwaboekqvkel\",\"displayName\":\"mvb\",\"displayDescription\":\"yjsflhhcaalnji\",\"unit\":\"sxyawjoyaqcs\",\"aggregationType\":\"jpkiidzyexznelix\",\"metricFilterPattern\":\"rzt\",\"dimensions\":[{},{}]}],\"logSpecifications\":[{\"name\":\"xknalaulppg\",\"displayName\":\"tpnapnyiropuhpig\",\"blobDuration\":\"gylgqgitxmedjvcs\"},{\"name\":\"n\",\"displayName\":\"wncwzzhxgktrmg\",\"blobDuration\":\"napkteoellw\"},{\"name\":\"fdygpfqbuaceopz\",\"displayName\":\"rhhuaopppcqeqx\",\"blobDuration\":\"z\"},{\"name\":\"hzxct\",\"displayName\":\"gbkdmoizpos\",\"blobDuration\":\"grcfb\"}]}}}]}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        PowerBIDedicatedManager manager = PowerBIDedicatedManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        PagedIterable<Operation> response = manager.operations().list(com.azure.core.util.Context.NONE);

        Assertions.assertEquals("hkfpbs", response.iterator().next().display().description());
        Assertions.assertEquals("mvb",
            response.iterator().next().properties().serviceSpecification().metricSpecifications().get(0).displayName());
        Assertions.assertEquals("yjsflhhcaalnji",
            response.iterator()
                .next()
                .properties()
                .serviceSpecification()
                .metricSpecifications()
                .get(0)
                .displayDescription());
        Assertions.assertEquals("tpnapnyiropuhpig",
            response.iterator().next().properties().serviceSpecification().logSpecifications().get(0).displayName());
    }
}
