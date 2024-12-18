// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.kusto.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.kusto.fluent.models.DatabaseInviteFollowerResultInner;
import org.junit.jupiter.api.Assertions;

public final class DatabaseInviteFollowerResultInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        DatabaseInviteFollowerResultInner model = BinaryData.fromString("{\"generatedInvitation\":\"c\"}")
            .toObject(DatabaseInviteFollowerResultInner.class);
        Assertions.assertEquals("c", model.generatedInvitation());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        DatabaseInviteFollowerResultInner model = new DatabaseInviteFollowerResultInner().withGeneratedInvitation("c");
        model = BinaryData.fromObject(model).toObject(DatabaseInviteFollowerResultInner.class);
        Assertions.assertEquals("c", model.generatedInvitation());
    }
}
