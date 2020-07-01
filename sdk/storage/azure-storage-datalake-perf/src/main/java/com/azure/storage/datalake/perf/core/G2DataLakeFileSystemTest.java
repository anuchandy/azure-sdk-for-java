// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.storage.datalake.perf.core;

import com.azure.perf.test.core.PerfStressOptions;

import com.azure.storage.file.datalake.DataLakeFileSystemAsyncClient;
import com.azure.storage.file.datalake.DataLakeFileSystemClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

public abstract class G2DataLakeFileSystemTest<TOptions extends PerfStressOptions> extends G2ServiceTest<TOptions> {
    private static final String FILE_SYSTEM_NAME = "perfstress-" + UUID.randomUUID().toString();

    protected final DataLakeFileSystemClient dataLakeFileSystemClient;
    protected final DataLakeFileSystemAsyncClient dataLakeFileSystemAsyncClient;

    public G2DataLakeFileSystemTest(TOptions options) {
        super(options);
        // Setup the file-system clients
        dataLakeFileSystemClient = dataLakeServiceClient.getFileSystemClient(FILE_SYSTEM_NAME);
        dataLakeFileSystemAsyncClient = dataLakeServiceAsyncClient.getFileSystemAsyncClient(FILE_SYSTEM_NAME);
    }

    // NOTE: the pattern setup the parent first, then yourself.
    @Override
    public Mono<Void> globalSetupAsync() {
        return super.globalSetupAsync().then(dataLakeFileSystemAsyncClient.create());
    }

    // NOTE: the pattern, cleanup yourself, then the parent.
    @Override
    public Mono<Void> globalCleanupAsync() {
        return dataLakeFileSystemAsyncClient.delete().then(super.globalCleanupAsync());
    }
}
