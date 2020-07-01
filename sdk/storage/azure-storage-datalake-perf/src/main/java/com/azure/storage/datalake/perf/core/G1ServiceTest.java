// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.storage.datalake.perf.core;

import com.azure.core.util.CoreUtils;
import com.azure.perf.test.core.PerfStressOptions;
import com.azure.perf.test.core.PerfStressTest;
import com.microsoft.azure.datalake.store.ADLStoreClient;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;

public abstract class G1ServiceTest<TOptions extends PerfStressOptions> extends PerfStressTest<TOptions> {
    protected ADLStoreClient adlStoreClient;

    /**
     * Creates an instance of performance test.
     *
     * @param options the options configured for the test.
     */
    public G1ServiceTest(TOptions options) {
        super(options);
        String endpoint = System.getenv("DATA_LAKE_ENDPOINT");
        if (CoreUtils.isNullOrEmpty(endpoint)) {
            System.out.println("Environment variable DATA_LAKE_ENDPOINT must be set");
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

        String authTokenEndpoint = "https://login.microsoftonline.com/" + tenantId + "/oauth2/token";
        AccessTokenProvider provider = new ClientCredsTokenProvider(authTokenEndpoint, clientId, clientSecret);
        adlStoreClient = ADLStoreClient.createClient(endpoint, provider);
    }
}
