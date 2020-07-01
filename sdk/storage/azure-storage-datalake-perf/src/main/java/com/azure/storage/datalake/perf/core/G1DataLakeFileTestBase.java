package com.azure.storage.datalake.perf.core;

import com.azure.perf.test.core.PerfStressOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public abstract class G1DataLakeFileTestBase<TOptions extends PerfStressOptions> extends G1DataLakeFileSystemTest<TOptions> {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    protected String fileName;

    public G1DataLakeFileTestBase(TOptions options) {
        super(options);
        this.fileName = "randomfiletest-" + UUID.randomUUID().toString();
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
