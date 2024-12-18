// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.sqlvirtualmachine.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.sqlvirtualmachine.models.DiskConfigurationType;
import com.azure.resourcemanager.sqlvirtualmachine.models.SqlStorageSettings;
import com.azure.resourcemanager.sqlvirtualmachine.models.SqlTempDbSettings;
import com.azure.resourcemanager.sqlvirtualmachine.models.StorageConfigurationSettings;
import com.azure.resourcemanager.sqlvirtualmachine.models.StorageWorkloadType;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class StorageConfigurationSettingsTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        StorageConfigurationSettings model = BinaryData.fromString(
            "{\"sqlDataSettings\":{\"luns\":[1911186108,2119975108],\"defaultFilePath\":\"mcmatuokthfuiu\"},\"sqlLogSettings\":{\"luns\":[680550088,2105680522,397485144,1099680690],\"defaultFilePath\":\"xodpuozmyzydagfu\"},\"sqlTempDbSettings\":{\"dataFileSize\":1613770943,\"dataGrowth\":45811298,\"logFileSize\":1016147904,\"logGrowth\":3069980,\"dataFileCount\":1285068717,\"persistFolder\":true,\"persistFolderPath\":\"dxwzywqsmbsurexi\",\"luns\":[998039935,1010959661,1027022233,994913433],\"defaultFilePath\":\"fksymddystki\"},\"sqlSystemDbOnDataDisk\":true,\"diskConfigurationType\":\"ADD\",\"storageWorkloadType\":\"OLTP\"}")
            .toObject(StorageConfigurationSettings.class);
        Assertions.assertEquals(1911186108, model.sqlDataSettings().luns().get(0));
        Assertions.assertEquals("mcmatuokthfuiu", model.sqlDataSettings().defaultFilePath());
        Assertions.assertEquals(680550088, model.sqlLogSettings().luns().get(0));
        Assertions.assertEquals("xodpuozmyzydagfu", model.sqlLogSettings().defaultFilePath());
        Assertions.assertEquals(1613770943, model.sqlTempDbSettings().dataFileSize());
        Assertions.assertEquals(45811298, model.sqlTempDbSettings().dataGrowth());
        Assertions.assertEquals(1016147904, model.sqlTempDbSettings().logFileSize());
        Assertions.assertEquals(3069980, model.sqlTempDbSettings().logGrowth());
        Assertions.assertEquals(1285068717, model.sqlTempDbSettings().dataFileCount());
        Assertions.assertEquals(true, model.sqlTempDbSettings().persistFolder());
        Assertions.assertEquals("dxwzywqsmbsurexi", model.sqlTempDbSettings().persistFolderPath());
        Assertions.assertEquals(998039935, model.sqlTempDbSettings().luns().get(0));
        Assertions.assertEquals("fksymddystki", model.sqlTempDbSettings().defaultFilePath());
        Assertions.assertEquals(true, model.sqlSystemDbOnDataDisk());
        Assertions.assertEquals(DiskConfigurationType.ADD, model.diskConfigurationType());
        Assertions.assertEquals(StorageWorkloadType.OLTP, model.storageWorkloadType());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        StorageConfigurationSettings model = new StorageConfigurationSettings()
            .withSqlDataSettings(new SqlStorageSettings().withLuns(Arrays.asList(1911186108, 2119975108))
                .withDefaultFilePath("mcmatuokthfuiu"))
            .withSqlLogSettings(
                new SqlStorageSettings().withLuns(Arrays.asList(680550088, 2105680522, 397485144, 1099680690))
                    .withDefaultFilePath("xodpuozmyzydagfu"))
            .withSqlTempDbSettings(new SqlTempDbSettings().withDataFileSize(1613770943)
                .withDataGrowth(45811298)
                .withLogFileSize(1016147904)
                .withLogGrowth(3069980)
                .withDataFileCount(1285068717)
                .withPersistFolder(true)
                .withPersistFolderPath("dxwzywqsmbsurexi")
                .withLuns(Arrays.asList(998039935, 1010959661, 1027022233, 994913433))
                .withDefaultFilePath("fksymddystki"))
            .withSqlSystemDbOnDataDisk(true)
            .withDiskConfigurationType(DiskConfigurationType.ADD)
            .withStorageWorkloadType(StorageWorkloadType.OLTP);
        model = BinaryData.fromObject(model).toObject(StorageConfigurationSettings.class);
        Assertions.assertEquals(1911186108, model.sqlDataSettings().luns().get(0));
        Assertions.assertEquals("mcmatuokthfuiu", model.sqlDataSettings().defaultFilePath());
        Assertions.assertEquals(680550088, model.sqlLogSettings().luns().get(0));
        Assertions.assertEquals("xodpuozmyzydagfu", model.sqlLogSettings().defaultFilePath());
        Assertions.assertEquals(1613770943, model.sqlTempDbSettings().dataFileSize());
        Assertions.assertEquals(45811298, model.sqlTempDbSettings().dataGrowth());
        Assertions.assertEquals(1016147904, model.sqlTempDbSettings().logFileSize());
        Assertions.assertEquals(3069980, model.sqlTempDbSettings().logGrowth());
        Assertions.assertEquals(1285068717, model.sqlTempDbSettings().dataFileCount());
        Assertions.assertEquals(true, model.sqlTempDbSettings().persistFolder());
        Assertions.assertEquals("dxwzywqsmbsurexi", model.sqlTempDbSettings().persistFolderPath());
        Assertions.assertEquals(998039935, model.sqlTempDbSettings().luns().get(0));
        Assertions.assertEquals("fksymddystki", model.sqlTempDbSettings().defaultFilePath());
        Assertions.assertEquals(true, model.sqlSystemDbOnDataDisk());
        Assertions.assertEquals(DiskConfigurationType.ADD, model.diskConfigurationType());
        Assertions.assertEquals(StorageWorkloadType.OLTP, model.storageWorkloadType());
    }
}
