// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.storage.blob.implementation.util;

import com.azure.core.util.logging.ClientLogger;
import org.junit.Assert;
import org.junit.Test;

public class StorageConnectionStringTest {
    private final ClientLogger logger = new ClientLogger(StorageConnectionStringTest.class);
    private static final String ACCOUNT_NAME_VALUE = "contoso";
    private static final String ACCOUNT_KEY_VALUE
            = "95o6TL9jkIjNr6HurD6Xa+zLQ+PX9/VWR8fI2ofHatbrUb8kRJ75B6enwRU3q1OP8fmjghaoxdqnwhN7m3pZow==";
    private static final String SAS_TOKEN
            = "sv=2015-07-08&sig=iCvQmdZngZNW%2F4vw43j6%2BVz6fndHF5LI639QJba4r8o%3D&spr=https" +
            "&st=2016-04-12T03%3A24%3A31Z" +
            "&se=2016-04-13T03%3A29%3A31Z&srt=s&ss=bf&sp=rwl";

    @Test
    public void sasToken() {
        final String blobEndpointStr = "https://storagesample.blob.core.windows.net";
        final String fileEndpointStr = "https://storagesample.file.core.windows.net";

        final String connectionString = String.format("BlobEndpoint=%s;FileEndpoint=%s;SharedAccessSignature=%s;",
                blobEndpointStr,
                fileEndpointStr,
                SAS_TOKEN);

        StorageConnectionString storageConnectionString = StorageConnectionString.create(connectionString, logger);
        Assert.assertNotNull(storageConnectionString);
        StorageConnectionString.StorageEndpoint blobEndpoint = storageConnectionString.getBlobEndpoint();
        Assert.assertNotNull(blobEndpoint);
        Assert.assertNotNull(blobEndpoint.getPrimaryUri());
        Assert.assertTrue(blobEndpoint.getPrimaryUri().toString().equalsIgnoreCase(blobEndpointStr));

        StorageConnectionString.StorageEndpoint fileEndpoint = storageConnectionString.getFileEndpoint();
        Assert.assertNotNull(fileEndpoint);
        Assert.assertNotNull(fileEndpoint.getPrimaryUri());
        Assert.assertTrue(fileEndpoint.getPrimaryUri().toString().equalsIgnoreCase(fileEndpointStr));

        Assert.assertNull(storageConnectionString.getQueueEndpoint());
        Assert.assertNull(storageConnectionString.getTableEndpoint());

        StorageConnectionString.StorageAuthenticationSettings authSettings
                = storageConnectionString.getStorageAuthSettings();
        Assert.assertNotNull(authSettings);
        Assert.assertEquals(StorageConnectionString.StorageAuthenticationSettings.Type.SAS_TOKEN,
                authSettings.getType());
        Assert.assertNotNull(authSettings.getSasToken());
        Assert.assertTrue(authSettings.getSasToken().equalsIgnoreCase(SAS_TOKEN));
        Assert.assertNull(storageConnectionString.getAccountName());
    }

    @Test
    public void accountNameKey() {
        final String connectionString = String.format("DefaultEndpointsProtocol=https;" +
                "AccountName=%s;" +
                "AccountKey=%s;", ACCOUNT_NAME_VALUE, ACCOUNT_KEY_VALUE);

        StorageConnectionString storageConnectionString = StorageConnectionString.create(connectionString, logger);
        Assert.assertNotNull(storageConnectionString);

        StorageConnectionString.StorageEndpoint blobEndpoint = storageConnectionString.getBlobEndpoint();
        Assert.assertNotNull(blobEndpoint);
        Assert.assertNotNull(blobEndpoint.getPrimaryUri());
        Assert.assertTrue(blobEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.blob.core.windows.net"));

        StorageConnectionString.StorageEndpoint fileEndpoint = storageConnectionString.getFileEndpoint();
        Assert.assertNotNull(fileEndpoint);
        Assert.assertNotNull(fileEndpoint.getPrimaryUri());
        Assert.assertTrue(fileEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.file.core.windows.net"));

        StorageConnectionString.StorageEndpoint queueEndpoint = storageConnectionString.getQueueEndpoint();
        Assert.assertNotNull(queueEndpoint);
        Assert.assertNotNull(queueEndpoint.getPrimaryUri());
        Assert.assertTrue(queueEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.queue.core.windows.net"));

        StorageConnectionString.StorageEndpoint tableEndpoint = storageConnectionString.getTableEndpoint();
        Assert.assertNotNull(tableEndpoint);
        Assert.assertNotNull(tableEndpoint.getPrimaryUri());
        Assert.assertTrue(tableEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.table.core.windows.net"));

        StorageConnectionString.StorageAuthenticationSettings authSettings = storageConnectionString.getStorageAuthSettings();
        Assert.assertNotNull(authSettings);
        Assert.assertEquals(StorageConnectionString.StorageAuthenticationSettings.Type.ACCOUNT_NAME_KEY,
                authSettings.getType());
        Assert.assertNotNull(authSettings.getAccount());
        Assert.assertNotNull(authSettings.getAccount().getName());
        Assert.assertNotNull(authSettings.getAccount().getAccessKey());
        Assert.assertTrue(authSettings.getAccount().getName().equals(ACCOUNT_NAME_VALUE));
        Assert.assertTrue(authSettings.getAccount().getAccessKey().equals(ACCOUNT_KEY_VALUE));
    }

    @Test
    public void customEndpointSuffix() {
        final String connectionString = String.format("DefaultEndpointsProtocol=https;" +
                "AccountName=%s;" +
                "AccountKey=%s;" +
                "EndpointSuffix=core.chinacloudapi.cn", ACCOUNT_NAME_VALUE, ACCOUNT_KEY_VALUE);

        StorageConnectionString storageConnectionString = StorageConnectionString.create(connectionString, logger);
        Assert.assertNotNull(storageConnectionString);

        StorageConnectionString.StorageEndpoint blobEndpoint = storageConnectionString.getBlobEndpoint();
        Assert.assertNotNull(blobEndpoint);
        Assert.assertNotNull(blobEndpoint.getPrimaryUri());
        Assert.assertTrue(blobEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.blob.core.chinacloudapi.cn"));

        StorageConnectionString.StorageEndpoint fileEndpoint = storageConnectionString.getFileEndpoint();
        Assert.assertNotNull(fileEndpoint);
        Assert.assertNotNull(fileEndpoint.getPrimaryUri());
        Assert.assertTrue(fileEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.file.core.chinacloudapi.cn"));

        StorageConnectionString.StorageEndpoint queueEndpoint = storageConnectionString.getQueueEndpoint();
        Assert.assertNotNull(queueEndpoint);
        Assert.assertNotNull(queueEndpoint.getPrimaryUri());
        Assert.assertTrue(queueEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.queue.core.chinacloudapi.cn"));

        StorageConnectionString.StorageEndpoint tableEndpoint = storageConnectionString.getTableEndpoint();
        Assert.assertNotNull(tableEndpoint);
        Assert.assertNotNull(tableEndpoint.getPrimaryUri());
        Assert.assertTrue(tableEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.table.core.chinacloudapi.cn"));

        StorageConnectionString.StorageAuthenticationSettings authSettings =
                storageConnectionString.getStorageAuthSettings();
        Assert.assertNotNull(authSettings);
        Assert.assertEquals(StorageConnectionString.StorageAuthenticationSettings.Type.ACCOUNT_NAME_KEY,
                authSettings.getType());
        Assert.assertNotNull(authSettings.getAccount());
        Assert.assertNotNull(authSettings.getAccount().getName());
        Assert.assertNotNull(authSettings.getAccount().getAccessKey());
        Assert.assertTrue(authSettings.getAccount().getName().equals(ACCOUNT_NAME_VALUE));
        Assert.assertTrue(authSettings.getAccount().getAccessKey().equals(ACCOUNT_KEY_VALUE));
    }

    @Test
    public void explicitEndpointsAndAccountName() {
        final String blobEndpointStr = "https://storagesample.blob.core.windows.net";
        final String fileEndpointStr = "https://storagesample.file.core.windows.net";
        final String connectionString = String.format("BlobEndpoint=%s;FileEndpoint=%s;AccountName=%s;AccountKey=%s",
                blobEndpointStr,
                fileEndpointStr,
                ACCOUNT_NAME_VALUE,
                ACCOUNT_KEY_VALUE);

        StorageConnectionString storageConnectionString = StorageConnectionString.create(connectionString, logger);
        Assert.assertNotNull(storageConnectionString);
        StorageConnectionString.StorageEndpoint blobEndpoint = storageConnectionString.getBlobEndpoint();
        Assert.assertNotNull(blobEndpoint);
        Assert.assertNotNull(blobEndpoint.getPrimaryUri());
        Assert.assertTrue(blobEndpoint.getPrimaryUri().toString().equalsIgnoreCase(blobEndpointStr));

        StorageConnectionString.StorageEndpoint fileEndpoint = storageConnectionString.getFileEndpoint();
        Assert.assertNotNull(fileEndpoint);
        Assert.assertNotNull(fileEndpoint.getPrimaryUri());
        Assert.assertTrue(fileEndpoint.getPrimaryUri().toString().equalsIgnoreCase(fileEndpointStr));

        StorageConnectionString.StorageEndpoint queueEndpoint = storageConnectionString.getQueueEndpoint();
        Assert.assertNotNull(queueEndpoint);
        Assert.assertNotNull(queueEndpoint.getPrimaryUri());
        Assert.assertTrue(queueEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.queue.core.windows.net"));

        StorageConnectionString.StorageEndpoint tableEndpoint = storageConnectionString.getTableEndpoint();
        Assert.assertNotNull(tableEndpoint);
        Assert.assertNotNull(tableEndpoint.getPrimaryUri());
        Assert.assertTrue(tableEndpoint.getPrimaryUri()
                .toString()
                .equalsIgnoreCase("https://contoso.table.core.windows.net"));

        StorageConnectionString.StorageAuthenticationSettings authSettings =
                storageConnectionString.getStorageAuthSettings();
        Assert.assertNotNull(authSettings);
        Assert.assertEquals(StorageConnectionString.StorageAuthenticationSettings.Type.ACCOUNT_NAME_KEY,
                authSettings.getType());
        Assert.assertNotNull(authSettings.getAccount());
        Assert.assertNotNull(authSettings.getAccount().getName());
        Assert.assertNotNull(authSettings.getAccount().getAccessKey());
        Assert.assertTrue(authSettings.getAccount().getName().equals(ACCOUNT_NAME_VALUE));
        Assert.assertTrue(authSettings.getAccount().getAccessKey().equals(ACCOUNT_KEY_VALUE));
    }

    @Test
    public void explicitEndpointsAndAnonymousAccess() {
        final String blobEndpointStr = "https://storagesample.blob.core.windows.net";
        final String fileEndpointStr = "https://storagesample.file.core.windows.net";

        final String connectionString = String.format("BlobEndpoint=%s;FileEndpoint=%s;",
                blobEndpointStr,
                fileEndpointStr);

        StorageConnectionString storageConnectionString = StorageConnectionString.create(connectionString, logger);
        Assert.assertNotNull(storageConnectionString);
        StorageConnectionString.StorageEndpoint blobEndpoint = storageConnectionString.getBlobEndpoint();
        Assert.assertNotNull(blobEndpoint);
        Assert.assertNotNull(blobEndpoint.getPrimaryUri());
        Assert.assertTrue(blobEndpoint.getPrimaryUri().toString().equalsIgnoreCase(blobEndpointStr));

        StorageConnectionString.StorageEndpoint fileEndpoint = storageConnectionString.getFileEndpoint();
        Assert.assertNotNull(fileEndpoint);
        Assert.assertNotNull(fileEndpoint.getPrimaryUri());
        Assert.assertTrue(fileEndpoint.getPrimaryUri().toString().equalsIgnoreCase(fileEndpointStr));

        StorageConnectionString.StorageEndpoint queueEndpoint = storageConnectionString.getQueueEndpoint();
        Assert.assertNull(queueEndpoint);

        StorageConnectionString.StorageEndpoint tableEndpoint = storageConnectionString.getTableEndpoint();
        Assert.assertNull(tableEndpoint);

        StorageConnectionString.StorageAuthenticationSettings authSettings
                = storageConnectionString.getStorageAuthSettings();
        Assert.assertNotNull(authSettings);
        Assert.assertEquals(StorageConnectionString.StorageAuthenticationSettings.Type.NONE,
                authSettings.getType());
        Assert.assertNull(authSettings.getAccount());
        Assert.assertNull(authSettings.getSasToken());
    }

    @Test
    public void skipEmptyEntries() {
        final String connectionString = "DefaultEndpointsProtocol=https;" +
                ";;" +
                "AccountName=" + ACCOUNT_NAME_VALUE + ";" +
                "AccountKey=" + ACCOUNT_KEY_VALUE + ";" +
                "EndpointSuffix=core.chinacloudapi.cn";
        StorageConnectionString.create(connectionString, logger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOrEmpty() {
        StorageConnectionString.create(null, logger);
        StorageConnectionString.create("", logger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingEqualDelimiter() {
        final String connectionString = "DefaultEndpointsProtocol=https;" +
                "AccountName=" + ACCOUNT_NAME_VALUE + ";" +
                "AccountKey" + ACCOUNT_KEY_VALUE + ";" + // Missing equal
                "EndpointSuffix=core.chinacloudapi.cn";
        StorageConnectionString.create(connectionString, logger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingKey() {
        final String connectionString = "DefaultEndpointsProtocol=https;" +
                "=" + ACCOUNT_NAME_VALUE + ";" + // Missing key
                "AccountKey=" + ACCOUNT_KEY_VALUE + ";" +
                "EndpointSuffix=core.chinacloudapi.cn";
        StorageConnectionString.create(connectionString, logger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingValue() {
        final String connectionString = "DefaultEndpointsProtocol=https;" +
                ACCOUNT_NAME_VALUE + "=;" + // Missing value
                "AccountKey=" + ACCOUNT_KEY_VALUE + ";" +
                "EndpointSuffix=core.chinacloudapi.cn";
        StorageConnectionString.create(connectionString, logger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingKeyValue() {
        final String connectionString = "DefaultEndpointsProtocol=https;" +
                "=;" +
                "AccountKey=" + ACCOUNT_KEY_VALUE + ";" +
                "EndpointSuffix=core.chinacloudapi.cn";
        StorageConnectionString.create(connectionString, logger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingAccountKey() {
        final String connectionString =
                "AccountName=" + ACCOUNT_NAME_VALUE + ";" +
                        ACCOUNT_KEY_VALUE + ";" +
                "EndpointSuffix=core.chinacloudapi.cn";
        StorageConnectionString.create(connectionString, logger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sasTokenAccountKeyMutuallyExclusive() {
        final String blobEndpointStr = "https://storagesample.blob.core.windows.net";
        final String fileEndpointStr = "https://storagesample.file.core.windows.net";

        final String connectionString =
                String.format("BlobEndpoint=%s;FileEndpoint=%s;SharedAccessSignature=%s;AccountName=%s;AccountKey=%s;",
                        blobEndpointStr,
                        fileEndpointStr,
                        SAS_TOKEN,
                        ACCOUNT_NAME_VALUE,
                        ACCOUNT_KEY_VALUE);
        StorageConnectionString.create(connectionString, logger);
    }
}
