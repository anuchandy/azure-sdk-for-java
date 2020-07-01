// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.storage.datalake.perf.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.azure.perf.test.core.PerfStressOptions;
import com.azure.storage.file.datalake.DataLakeFileAsyncClient;
import com.azure.storage.file.datalake.DataLakeFileClient;

public abstract class G2DataLakeFileTestBase<TOptions extends PerfStressOptions> extends G2DataLakeFileSystemTest<TOptions> {

    public static final int DEFAULT_BUFFER_SIZE = 8192;
    protected final DataLakeFileClient fileClient;
    protected final DataLakeFileAsyncClient fileAsyncClient;

    public G2DataLakeFileTestBase(TOptions options) {
        super(options);
        String fileName = "randomfiletest-" + UUID.randomUUID().toString();
        fileClient = dataLakeFileSystemClient.getFileClient(fileName);
        fileAsyncClient = dataLakeFileSystemAsyncClient.getFileAsyncClient(fileName);
    }

    public long copyStream(InputStream input, OutputStream out) throws IOException {
        long transferred = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = input.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, read);
            transferred += read;
        }
        return transferred;
    }
}
