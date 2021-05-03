// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.models;

import com.azure.core.annotation.Immutable;

/**
 * The AzureDataLakeStorageGen2DataFeedSource model.
 */
@Immutable
public final class AzureDataLakeStorageGen2DataFeedSource extends DataFeedSource {
    /*
     * The storage account
     */
    private final AzureDataLakeStorageGen2DataSourceAccount storageAccount;

    /*
     * File system name (Container)
     */
    private final String fileSystemName;

    /*
     * Directory template
     */
    private final String directoryTemplate;

    /*
     * File template
     */
    private final String fileTemplate;

    /**
     * Constructs a AzureDataLakeStorageGen2DataFeedSource object.
     *
     * @param accountName the name of the storage account.
     * @param accountKey the key of the storage account.
     * @param fileSystemName the file system name.
     * @param directoryTemplate the directoty template of the storage account.
     * @param fileTemplate the file template.
     */
    public AzureDataLakeStorageGen2DataFeedSource(final String accountName, final String accountKey,
        final String fileSystemName, final String directoryTemplate, final String fileTemplate) {
        this(AzureDataLakeStorageGen2DataSourceAccount.withBasicCredentials(accountName, accountKey),
            fileSystemName,
            directoryTemplate,
            fileTemplate);
    }

    /**
     * Constructs a AzureDataLakeStorageGen2DataFeedSource object.
     *
     * @param storageAccount the the storage account.
     * @param fileSystemName the file system name.
     * @param directoryTemplate the directoty template of the storage account.
     * @param fileTemplate the file template.
     */
    public AzureDataLakeStorageGen2DataFeedSource(final AzureDataLakeStorageGen2DataSourceAccount storageAccount,
                                                  final String fileSystemName, final String directoryTemplate,
                                                  final String fileTemplate) {
        this.storageAccount = storageAccount;
        this.fileSystemName = fileSystemName;
        this.directoryTemplate = directoryTemplate;
        this.fileTemplate = fileTemplate;
    }

    /**
     * Get the the account for the AzureDataLakeStorageGen2DataFeedSource.
     *
     * @return the account value.
     */
    public AzureDataLakeStorageGen2DataSourceAccount getStorageAccount() {
        return this.storageAccount;
    }

    /**
     * Get the file system name or the container name.
     *
     * @return the fileSystemName value.
     */
    public String getFileSystemName() {
        return this.fileSystemName;
    }

    /**
     * Get the directory template.
     *
     * @return the directoryTemplate value.
     */
    public String getDirectoryTemplate() {
        return this.directoryTemplate;
    }

    /**
     * Get the file template.
     *
     * @return the fileTemplate value.
     */
    public String getFileTemplate() {
        return this.fileTemplate;
    }
}
