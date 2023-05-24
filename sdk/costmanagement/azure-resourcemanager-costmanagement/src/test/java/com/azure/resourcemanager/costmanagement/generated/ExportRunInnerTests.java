// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.costmanagement.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.costmanagement.fluent.models.CommonExportPropertiesInner;
import com.azure.resourcemanager.costmanagement.fluent.models.ExportRunInner;
import com.azure.resourcemanager.costmanagement.models.ExecutionStatus;
import com.azure.resourcemanager.costmanagement.models.ExecutionType;
import com.azure.resourcemanager.costmanagement.models.FormatType;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;

public final class ExportRunInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ExportRunInner model =
            BinaryData
                .fromString(
                    "{\"properties\":{\"executionType\":\"Scheduled\",\"status\":\"Failed\",\"submittedBy\":\"oyzko\",\"submittedTime\":\"2021-06-11T18:07:16Z\",\"processingStartTime\":\"2021-01-01T04:06:13Z\",\"processingEndTime\":\"2021-11-23T22:05:35Z\",\"fileName\":\"xawqaldsyuuxim\",\"runSettings\":{\"format\":\"Csv\",\"partitionData\":true,\"nextRunTimeEstimate\":\"2021-08-20T07:27:40Z\"}},\"eTag\":\"bykutw\",\"id\":\"fhpagmhrskdsnf\",\"name\":\"sd\",\"type\":\"akgtdlmkkzevdlh\"}")
                .toObject(ExportRunInner.class);
        Assertions.assertEquals("bykutw", model.etag());
        Assertions.assertEquals(ExecutionType.SCHEDULED, model.executionType());
        Assertions.assertEquals(ExecutionStatus.FAILED, model.status());
        Assertions.assertEquals("oyzko", model.submittedBy());
        Assertions.assertEquals(OffsetDateTime.parse("2021-06-11T18:07:16Z"), model.submittedTime());
        Assertions.assertEquals(OffsetDateTime.parse("2021-01-01T04:06:13Z"), model.processingStartTime());
        Assertions.assertEquals(OffsetDateTime.parse("2021-11-23T22:05:35Z"), model.processingEndTime());
        Assertions.assertEquals("xawqaldsyuuxim", model.fileName());
        Assertions.assertEquals(FormatType.CSV, model.runSettings().format());
        Assertions.assertEquals(true, model.runSettings().partitionData());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ExportRunInner model =
            new ExportRunInner()
                .withEtag("bykutw")
                .withExecutionType(ExecutionType.SCHEDULED)
                .withStatus(ExecutionStatus.FAILED)
                .withSubmittedBy("oyzko")
                .withSubmittedTime(OffsetDateTime.parse("2021-06-11T18:07:16Z"))
                .withProcessingStartTime(OffsetDateTime.parse("2021-01-01T04:06:13Z"))
                .withProcessingEndTime(OffsetDateTime.parse("2021-11-23T22:05:35Z"))
                .withFileName("xawqaldsyuuxim")
                .withRunSettings(new CommonExportPropertiesInner().withFormat(FormatType.CSV).withPartitionData(true));
        model = BinaryData.fromObject(model).toObject(ExportRunInner.class);
        Assertions.assertEquals("bykutw", model.etag());
        Assertions.assertEquals(ExecutionType.SCHEDULED, model.executionType());
        Assertions.assertEquals(ExecutionStatus.FAILED, model.status());
        Assertions.assertEquals("oyzko", model.submittedBy());
        Assertions.assertEquals(OffsetDateTime.parse("2021-06-11T18:07:16Z"), model.submittedTime());
        Assertions.assertEquals(OffsetDateTime.parse("2021-01-01T04:06:13Z"), model.processingStartTime());
        Assertions.assertEquals(OffsetDateTime.parse("2021-11-23T22:05:35Z"), model.processingEndTime());
        Assertions.assertEquals("xawqaldsyuuxim", model.fileName());
        Assertions.assertEquals(FormatType.CSV, model.runSettings().format());
        Assertions.assertEquals(true, model.runSettings().partitionData());
    }
}