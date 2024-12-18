// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.sqlvirtualmachine.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.sqlvirtualmachine.models.OperationListResult;

public final class OperationListResultTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        OperationListResult model = BinaryData.fromString(
            "{\"value\":[{\"name\":\"yhuybbkpod\",\"display\":{\"provider\":\"oginuvamiheognar\",\"resource\":\"xth\",\"operation\":\"tusivyevcciqihn\",\"description\":\"ngbwjz\"},\"origin\":\"system\",\"properties\":{\"ljofxqeofjaeqjh\":\"dataxgispemvtzfkufu\"}},{\"name\":\"b\",\"display\":{\"provider\":\"msmjqulngsntn\",\"resource\":\"bkzgcwrwclx\",\"operation\":\"rljdouskcqv\",\"description\":\"cr\"},\"origin\":\"user\",\"properties\":{\"biksq\":\"datatnhxbn\",\"ainqpjwnzlljfm\":\"datagls\",\"vmgxsab\":\"datapee\",\"jczdzevndh\":\"datayqduujit\"}},{\"name\":\"wpdappdsbdkv\",\"display\":{\"provider\":\"jfeusnh\",\"resource\":\"je\",\"operation\":\"mrldhu\",\"description\":\"zzd\"},\"origin\":\"system\",\"properties\":{\"geablgphuticndvk\":\"dataoc\",\"ftyxolniw\":\"dataozwyiftyhxhuro\"}},{\"name\":\"cukjf\",\"display\":{\"provider\":\"awxklr\",\"resource\":\"lwckbasyypnddhs\",\"operation\":\"bacphejko\",\"description\":\"nqgoulzndli\"},\"origin\":\"user\",\"properties\":{\"dgak\":\"datagfgibm\"}}],\"nextLink\":\"s\"}")
            .toObject(OperationListResult.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        OperationListResult model = new OperationListResult();
        model = BinaryData.fromObject(model).toObject(OperationListResult.class);
    }
}
