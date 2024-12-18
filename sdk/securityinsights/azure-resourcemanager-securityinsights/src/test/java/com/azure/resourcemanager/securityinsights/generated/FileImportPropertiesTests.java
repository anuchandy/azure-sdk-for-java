// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.securityinsights.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.securityinsights.fluent.models.FileImportProperties;
import com.azure.resourcemanager.securityinsights.models.FileFormat;
import com.azure.resourcemanager.securityinsights.models.FileImportContentType;
import com.azure.resourcemanager.securityinsights.models.FileMetadata;
import com.azure.resourcemanager.securityinsights.models.IngestionMode;
import org.junit.jupiter.api.Assertions;

public final class FileImportPropertiesTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        FileImportProperties model = BinaryData.fromString(
            "{\"ingestionMode\":\"IngestAnyValidRecords\",\"contentType\":\"BasicIndicator\",\"createdTimeUTC\":\"2021-06-01T02:29:17Z\",\"errorFile\":{\"fileFormat\":\"Unspecified\",\"fileName\":\"fgdo\",\"fileSize\":1738680873,\"fileContentUri\":\"iipuipwoqonm\",\"deleteStatus\":\"Unspecified\"},\"errorsPreview\":[{\"recordIndex\":863821322,\"errorMessages\":[\"hqvcimpevfgmblr\",\"ilbywdxsm\",\"ccwr\"]}],\"importFile\":{\"fileFormat\":\"JSON\",\"fileName\":\"jfnynszqujizdvoq\",\"fileSize\":1634271875,\"fileContentUri\":\"yo\",\"deleteStatus\":\"Deleted\"},\"ingestedRecordCount\":1293894339,\"source\":\"yavutpthjoxois\",\"state\":\"Ingested\",\"totalRecordCount\":1976615224,\"validRecordCount\":791689891,\"filesValidUntilTimeUTC\":\"2021-06-19T14:04:44Z\",\"importValidUntilTimeUTC\":\"2021-08-20T19:57:24Z\"}")
            .toObject(FileImportProperties.class);
        Assertions.assertEquals(IngestionMode.INGEST_ANY_VALID_RECORDS, model.ingestionMode());
        Assertions.assertEquals(FileImportContentType.BASIC_INDICATOR, model.contentType());
        Assertions.assertEquals(FileFormat.JSON, model.importFile().fileFormat());
        Assertions.assertEquals("jfnynszqujizdvoq", model.importFile().fileName());
        Assertions.assertEquals(1634271875, model.importFile().fileSize());
        Assertions.assertEquals("yavutpthjoxois", model.source());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        FileImportProperties model
            = new FileImportProperties().withIngestionMode(IngestionMode.INGEST_ANY_VALID_RECORDS)
                .withContentType(FileImportContentType.BASIC_INDICATOR)
                .withImportFile(new FileMetadata().withFileFormat(FileFormat.JSON)
                    .withFileName("jfnynszqujizdvoq")
                    .withFileSize(1634271875))
                .withSource("yavutpthjoxois");
        model = BinaryData.fromObject(model).toObject(FileImportProperties.class);
        Assertions.assertEquals(IngestionMode.INGEST_ANY_VALID_RECORDS, model.ingestionMode());
        Assertions.assertEquals(FileImportContentType.BASIC_INDICATOR, model.contentType());
        Assertions.assertEquals(FileFormat.JSON, model.importFile().fileFormat());
        Assertions.assertEquals("jfnynszqujizdvoq", model.importFile().fileName());
        Assertions.assertEquals(1634271875, model.importFile().fileSize());
        Assertions.assertEquals("yavutpthjoxois", model.source());
    }
}
