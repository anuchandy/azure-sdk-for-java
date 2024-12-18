// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.fluent.models.TransparentDataEncryptionInner;
import com.azure.resourcemanager.synapse.models.TransparentDataEncryptionStatus;
import org.junit.jupiter.api.Assertions;

public final class TransparentDataEncryptionInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        TransparentDataEncryptionInner model = BinaryData.fromString(
            "{\"location\":\"whojvp\",\"properties\":{\"status\":\"Disabled\"},\"id\":\"xysmoc\",\"name\":\"bq\",\"type\":\"qvmkcxo\"}")
            .toObject(TransparentDataEncryptionInner.class);
        Assertions.assertEquals(TransparentDataEncryptionStatus.DISABLED, model.status());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        TransparentDataEncryptionInner model
            = new TransparentDataEncryptionInner().withStatus(TransparentDataEncryptionStatus.DISABLED);
        model = BinaryData.fromObject(model).toObject(TransparentDataEncryptionInner.class);
        Assertions.assertEquals(TransparentDataEncryptionStatus.DISABLED, model.status());
    }
}
