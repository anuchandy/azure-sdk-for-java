// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.timeseriesinsights.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.timeseriesinsights.models.OperationListResult;

public final class OperationListResultTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        OperationListResult model = BinaryData.fromString(
            "{\"value\":[{\"name\":\"quvgjxpybczme\",\"display\":{\"provider\":\"zopbsphrupidgs\",\"resource\":\"bejhphoycmsxa\",\"operation\":\"hdxbmtqio\",\"description\":\"zehtbmu\"},\"origin\":\"ownoizhw\",\"properties\":{}},{\"name\":\"bqsoqijg\",\"display\":{\"provider\":\"bpazlobcufpdzn\",\"resource\":\"t\",\"operation\":\"qjnqglhqgnufoooj\",\"description\":\"ifsqesaagdfmg\"},\"origin\":\"lhjxr\",\"properties\":{}},{\"name\":\"mrvktsizntoc\",\"display\":{\"provider\":\"ouajpsqucmpoyf\",\"resource\":\"fogknygjofjdde\",\"operation\":\"rd\",\"description\":\"pewnw\"},\"origin\":\"itjz\",\"properties\":{}}],\"nextLink\":\"sarhmofc\"}")
            .toObject(OperationListResult.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        OperationListResult model = new OperationListResult();
        model = BinaryData.fromObject(model).toObject(OperationListResult.class);
    }
}
