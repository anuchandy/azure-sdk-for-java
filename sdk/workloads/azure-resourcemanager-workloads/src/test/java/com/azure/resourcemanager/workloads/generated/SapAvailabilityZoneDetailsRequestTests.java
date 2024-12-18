// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.workloads.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.workloads.models.SapAvailabilityZoneDetailsRequest;
import com.azure.resourcemanager.workloads.models.SapDatabaseType;
import com.azure.resourcemanager.workloads.models.SapProductType;
import org.junit.jupiter.api.Assertions;

public final class SapAvailabilityZoneDetailsRequestTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        SapAvailabilityZoneDetailsRequest model = BinaryData
            .fromString("{\"appLocation\":\"zufcyzkohdbi\",\"sapProduct\":\"Other\",\"databaseType\":\"HANA\"}")
            .toObject(SapAvailabilityZoneDetailsRequest.class);
        Assertions.assertEquals("zufcyzkohdbi", model.appLocation());
        Assertions.assertEquals(SapProductType.OTHER, model.sapProduct());
        Assertions.assertEquals(SapDatabaseType.HANA, model.databaseType());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        SapAvailabilityZoneDetailsRequest model
            = new SapAvailabilityZoneDetailsRequest().withAppLocation("zufcyzkohdbi")
                .withSapProduct(SapProductType.OTHER)
                .withDatabaseType(SapDatabaseType.HANA);
        model = BinaryData.fromObject(model).toObject(SapAvailabilityZoneDetailsRequest.class);
        Assertions.assertEquals("zufcyzkohdbi", model.appLocation());
        Assertions.assertEquals(SapProductType.OTHER, model.sapProduct());
        Assertions.assertEquals(SapDatabaseType.HANA, model.databaseType());
    }
}
