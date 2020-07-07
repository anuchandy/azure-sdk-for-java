// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.storage.datalake.perf.core;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.util.CoreUtils;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.perf.test.core.PerfStressHttpClient;
import com.azure.perf.test.core.PerfStressOptions;
import com.azure.perf.test.core.PerfStressTest;
import com.azure.storage.file.datalake.DataLakeServiceAsyncClient;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;

public abstract class G2ServiceTest<TOptions extends PerfStressOptions> extends PerfStressTest<TOptions> {

    protected final DataLakeServiceClient dataLakeServiceClient;
    protected final DataLakeServiceAsyncClient dataLakeServiceAsyncClient;

    public G2ServiceTest(TOptions options) {
        super(options);
        String endpoint = System.getenv("STORAGE_DATA_LAKE_ENDPOINT");
        if (CoreUtils.isNullOrEmpty(endpoint)) {
            System.out.println("Environment variable STORAGE_DATA_LAKE_ENDPOINT must be set");
            System.exit(1);
        }
        String tenantId = System.getenv("AAD_TENANT_ID");
        if (CoreUtils.isNullOrEmpty(tenantId)) {
            System.out.println("Environment variable AAD_TENANT_ID must be set");
            System.exit(1);
        }
        String clientId = System.getenv("AAD_CLIENT_ID");
        if (CoreUtils.isNullOrEmpty(clientId)) {
            System.out.println("Environment variable AAD_CLIENT_ID must be set");
            System.exit(1);
        }
        String clientSecret = System.getenv("AAD_CLIENT_SECRET");
        if (CoreUtils.isNullOrEmpty(clientSecret)) {
            System.out.println("Environment variable AAD_CLIENT_SECRET must be set");
            System.exit(1);
        }

        TokenCredential tokenCredential = new ClientSecretCredentialBuilder()
            .tenantId(tenantId)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build();

        // Setup the service client
        DataLakeServiceClientBuilder builder = new DataLakeServiceClientBuilder()
            .endpoint(endpoint)
            .credential(tokenCredential)
            .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC))
            .httpClient(PerfStressHttpClient.create(options));

        dataLakeServiceClient = builder.buildClient();
        dataLakeServiceAsyncClient = builder.buildAsyncClient();
    }
}
