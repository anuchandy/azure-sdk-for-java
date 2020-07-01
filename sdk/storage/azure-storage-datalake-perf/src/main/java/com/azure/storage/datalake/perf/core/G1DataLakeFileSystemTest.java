// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.storage.datalake.perf.core;

import com.azure.perf.test.core.PerfStressOptions;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class G1DataLakeFileSystemTest<TOptions extends PerfStressOptions> extends G1ServiceTest<TOptions> {
    private static final String FILE_SYSTEM_NAME = "perfstress-" + UUID.randomUUID().toString();

    public G1DataLakeFileSystemTest(TOptions options) {
        super(options);
    }

    // NOTE: the pattern setup the parent first, then yourself.
    @Override
    public Mono<Void> globalSetupAsync() {
        return super.globalSetupAsync().then(Mono.fromCallable(() -> {
            adlStoreClient.createDirectory(FILE_SYSTEM_NAME);
            return null;
        }));
    }

    // NOTE: the pattern, cleanup yourself, then the parent.
    @Override
    public Mono<Void> globalCleanupAsync() {
        return
            Mono.fromCallable((Callable<Void>) () -> {
                adlStoreClient.deleteRecursive(FILE_SYSTEM_NAME);
                return null;
            }).then(super.globalCleanupAsync());
    }
}
