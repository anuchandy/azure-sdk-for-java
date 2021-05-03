package com.azure.ai.metricsadvisor.implementation.util;

import com.azure.ai.metricsadvisor.models.AzureDataLakeStorageGen2DataSourceAccount;
import com.azure.ai.metricsadvisor.models.DataFeed;

public final class AzureDataLakeStorageGen2DataSourceAccountHelper {
    private static AzureDataLakeStorageGen2DataSourceAccountAccessor accessor;

    private AzureDataLakeStorageGen2DataSourceAccountHelper() { }

    /**
     * Type defining the methods to set the non-public properties of an
     * {@link AzureDataLakeStorageGen2DataSourceAccount} instance.
     */
    public interface AzureDataLakeStorageGen2DataSourceAccountAccessor {
        void setId(AzureDataLakeStorageGen2DataSourceAccount feed, String id);
        String getAccountKey(AzureDataLakeStorageGen2DataSourceAccount feed);
    }

    /**
     * The method called from {@link DataFeed} to set it's accessor.
     *
     * @param accountAccessor The accessor.
     */
    public static void setAccessor(final AzureDataLakeStorageGen2DataSourceAccountAccessor accountAccessor) {
        accessor = accountAccessor;
    }

    static void setId(AzureDataLakeStorageGen2DataSourceAccount account, String id) {
        accessor.setId(account, id);
    }

    static String getAccountKey(AzureDataLakeStorageGen2DataSourceAccount account) {
        return accessor.getAccountKey(account);
    }
}
