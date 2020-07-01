package com.azure.storage.datalake.perf;


import com.azure.perf.test.core.PerfStressOptions;
import com.azure.storage.datalake.perf.core.G1DataLakeFileTestBase;
import com.microsoft.azure.datalake.store.ADLFileOutputStream;
import com.microsoft.azure.datalake.store.IfExists;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.azure.perf.test.core.TestDataCreationHelper.createRandomInputStream;

public class G1UploadFromFileTest extends G1DataLakeFileTestBase<PerfStressOptions> {
    private static final Path TEMP_FILE;

    static {
        try {
            TEMP_FILE = Files.createTempFile(null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public G1UploadFromFileTest(PerfStressOptions options) {
        super(options);
    }

    @Override
    public Mono<Void> globalSetupAsync() {
        return super.globalSetupAsync().then(createTempFile());
    }

    @Override
    public Mono<Void> globalCleanupAsync() {
        return deleteTempFile().then(super.globalCleanupAsync());
    }

    private Mono<Void> createTempFile() {
        try (InputStream inputStream = createRandomInputStream(options.getSize());
             OutputStream outputStream = new FileOutputStream(TEMP_FILE.toString())) {
            copyStream(inputStream, outputStream);
            return Mono.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Mono<Void> deleteTempFile() {
        try {
            Files.delete(TEMP_FILE);
            return Mono.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            ADLFileOutputStream outStream
                = adlStoreClient.createFile(super.fileName, IfExists.OVERWRITE);
            InputStream inputStream = new FileInputStream(TEMP_FILE.toString());
            copyStream(inputStream, outStream);
            outStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public Mono<Void> runAsync() {
        return Mono.error(new RuntimeException("NA: Not Implemented"));
    }
}

