// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The AzureDataLakeStorageGen2ParameterPatch model. */
@Fluent
public final class AzureDataLakeStorageGen2ParameterPatch {
    /*
     * The account name of this Azure Data Lake
     */
    @JsonProperty(value = "accountName")
    private Option<String> accountName;

    /*
     * The account key that can access this Azure Data Lake
     */
    @JsonProperty(value = "accountKey")
    private Option<String> accountKey;

    /*
     * The file system (container) name in this Azure Data Lake
     */
    @JsonProperty(value = "fileSystemName")
    private Option<String> fileSystemName;

    /*
     * The directory template under this file system
     */
    @JsonProperty(value = "directoryTemplate")
    private Option<String> directoryTemplate;

    /*
     * The file template
     */
    @JsonProperty(value = "fileTemplate")
    private Option<String> fileTemplate;

    /**
     * Set the accountName property: The account name of this Azure Data Lake.
     *
     * @param accountName the accountName value to set.
     * @return the AzureDataLakeStorageGen2ParameterPatch object itself.
     */
    public AzureDataLakeStorageGen2ParameterPatch setAccountName(String accountName) {
        if (accountName == null) {
            this.accountName = Option.empty();
        } else {
            this.accountName = Option.of(accountName);
        }
        return this;
    }

    /**
     * Set the accountKey property: The account key that can access this Azure Data Lake.
     *
     * @param accountKey the accountKey value to set.
     * @return the AzureDataLakeStorageGen2ParameterPatch object itself.
     */
    public AzureDataLakeStorageGen2ParameterPatch setAccountKey(String accountKey) {
        if (accountKey == null) {
            this.accountKey = Option.empty();
        } else {
            this.accountKey = Option.of(accountKey);
        }
        return this;
    }

    /**
     * Set the fileSystemName property: The file system (container) name in this Azure Data Lake.
     *
     * @param fileSystemName the fileSystemName value to set.
     * @return the AzureDataLakeStorageGen2ParameterPatch object itself.
     */
    public AzureDataLakeStorageGen2ParameterPatch setFileSystemName(String fileSystemName) {
        if (fileSystemName == null) {
            this.fileSystemName = Option.empty();
        } else {
            this.fileSystemName = Option.of(fileSystemName);
        }
        return this;
    }

    /**
     * Set the directoryTemplate property: The directory template under this file system.
     *
     * @param directoryTemplate the directoryTemplate value to set.
     * @return the AzureDataLakeStorageGen2ParameterPatch object itself.
     */
    public AzureDataLakeStorageGen2ParameterPatch setDirectoryTemplate(String directoryTemplate) {
        if (directoryTemplate == null) {
            this.directoryTemplate = Option.empty();
        } else {
            this.directoryTemplate = Option.of(directoryTemplate);
        }
        return this;
    }

    /**
     * Set the fileTemplate property: The file template.
     *
     * @param fileTemplate the fileTemplate value to set.
     * @return the AzureDataLakeStorageGen2ParameterPatch object itself.
     */
    public AzureDataLakeStorageGen2ParameterPatch setFileTemplate(String fileTemplate) {
        if (fileTemplate == null) {
            this.fileTemplate = Option.empty();
        } else {
            this.fileTemplate = Option.of(fileTemplate);
        }
        return this;
    }
}
