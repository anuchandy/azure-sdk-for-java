// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.fluent.models.SqlPoolUsageInner;
import com.azure.resourcemanager.synapse.models.SqlPoolUsageListResult;
import java.util.Arrays;

public final class SqlPoolUsageListResultTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        SqlPoolUsageListResult model = BinaryData.fromString(
            "{\"value\":[{\"name\":\"rwyhqmibzyhwitsm\",\"resourceName\":\"yynpcdpumnzgmwz\",\"displayName\":\"abikns\",\"currentValue\":6.618364675171895,\"limit\":92.09110647306585,\"unit\":\"dtlwwrlkd\",\"nextResetTime\":\"2021-01-27T13:38:37Z\"},{\"name\":\"vokotllxdyh\",\"resourceName\":\"y\",\"displayName\":\"ogjltdtbnnhad\",\"currentValue\":62.09409186801451,\"limit\":77.71998697253709,\"unit\":\"khnvpam\",\"nextResetTime\":\"2021-05-20T03:12:36Z\"},{\"name\":\"queziky\",\"resourceName\":\"gxk\",\"displayName\":\"la\",\"currentValue\":43.550551381865965,\"limit\":53.32288334528516,\"unit\":\"iccjzkzivgvvcna\",\"nextResetTime\":\"2021-09-08T02:15:33Z\"},{\"name\":\"rnxxmueed\",\"resourceName\":\"rdvstkwqqtch\",\"displayName\":\"lmfmtdaay\",\"currentValue\":1.7891473583619977,\"limit\":89.32689226569329,\"unit\":\"ohgwxrtfudxepxg\",\"nextResetTime\":\"2020-12-22T18:51:20Z\"}],\"nextLink\":\"vrvmnpkukghim\"}")
            .toObject(SqlPoolUsageListResult.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        SqlPoolUsageListResult model = new SqlPoolUsageListResult().withValue(Arrays.asList(new SqlPoolUsageInner(),
            new SqlPoolUsageInner(), new SqlPoolUsageInner(), new SqlPoolUsageInner()));
        model = BinaryData.fromObject(model).toObject(SqlPoolUsageListResult.class);
    }
}
