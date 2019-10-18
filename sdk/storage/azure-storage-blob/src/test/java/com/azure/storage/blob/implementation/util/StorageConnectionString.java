// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.storage.blob.implementation.util;

import com.azure.core.util.logging.ClientLogger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Type representing storage connection string.
 *
 * RESERVED FOR INTERNAL USE.
 */
public final class StorageConnectionString {
    /**
     * The storage account name.
     */
    private final String accountName;

    /**
     * The settings for storage authentication.
     */
    private StorageAuthenticationSettings storageAuthSettings;

    /**
     * The blob endpoint.
     */
    private final StorageEndpoint blobEndpoint;

    /**
     * The file endpoint.
     */
    private final StorageEndpoint fileEndpoint;

    /**
     * The queue endpoint.
     */
    private final StorageEndpoint queueEndpoint;

    /**
     * The table endpoint.
     */
    private final StorageEndpoint tableEndpoint;

    /**
     * @return the storage account name.
     */
    public String getAccountName() {
        return this.accountName;
    }

    /**
     * @return The storage authentication settings associated with this connection string.
     */
    public StorageAuthenticationSettings getStorageAuthSettings() {
        return this.storageAuthSettings;
    }

    /**
     * Get the endpoint for the storage blob service.
     *
     * @return the blob endpoint associated with this connection string.
     */
    public StorageEndpoint getBlobEndpoint() {
        return this.blobEndpoint;
    }

    /**
     * Get the endpoint for the storage file service.
     *
     * @return the file endpoint associated with this connection string.
     */
    public StorageEndpoint getFileEndpoint() {
        return this.fileEndpoint;
    }

    /**
     * Get the endpoint for the storage queue service.
     *
     * @return the queue endpoint associated with this connection string.
     */
    public StorageEndpoint getQueueEndpoint() {
        return this.queueEndpoint;
    }

    /**
     * Get the endpoint for the storage table service.
     *
     * @return the table endpoint associated with this connection string.
     */
    public StorageEndpoint getTableEndpoint() {
        return this.tableEndpoint;
    }

    /**
     * Create a {@link StorageConnectionString} from the given connection string.
     *
     * @param connectionString the connection string
     * @param logger the logger
     * @return StorageConnectionString based on the provided connection string.
     */
    public static StorageConnectionString create(final String connectionString, final ClientLogger logger) {
        if (isNullOrEmpty(connectionString)) {
            throw logger.logExceptionAsError(new IllegalArgumentException("Invalid connection string."));
        }
        final ConnectionSettings settings = ConnectionSettings.fromConnectionString(connectionString, logger);
        StorageConnectionString storageConnectionString = tryCreateForEmulator(settings, logger);
        if (storageConnectionString != null) {
            return storageConnectionString;
        }
        storageConnectionString = tryCreateForService(settings, logger);
        if (storageConnectionString != null) {
            return storageConnectionString;
        }
        throw logger.logExceptionAsError(new IllegalArgumentException("Invalid connection string."));
    }

    private static StorageConnectionString tryCreateForEmulator(final ConnectionSettings settings,
                                                                final ClientLogger logger) {
        if (ConnectionSettingsFilter.matchesSpecification(settings,
                ConnectionSettingsFilter.allRequired(Constants.USE_EMULATOR_STORAGE_NAME),
                ConnectionSettingsFilter.optional(Constants.EMULATOR_STORAGE_PROXY_URI_NAME))) {
            if (!Boolean.parseBoolean(settings.getSettingValue(Constants.USE_EMULATOR_STORAGE_NAME))) {
                throw logger.logExceptionAsError(new IllegalArgumentException("Invalid connection string, " +
                        "the 'UseDevelopmentStorage' key must always have the value 'true'. " +
                        "Remove the flag entirely otherwise."));
            }
            String scheme;
            String host;
            if (settings.hasSetting(Constants.EMULATOR_STORAGE_PROXY_URI_NAME)) {
                try {
                    URI devStoreProxyUri = new URI(settings.getSettingValue(Constants.EMULATOR_STORAGE_PROXY_URI_NAME));
                    scheme = devStoreProxyUri.getScheme();
                    host = devStoreProxyUri.getHost();
                } catch (URISyntaxException use) {
                    throw logger.logExceptionAsError(new RuntimeException(use));
                }
            } else {
                scheme = "http";
                host = "127.0.0.1";
            }

            StorageConnectionString account;
            try {
                URI blobPrimaryEndpoint = new URI(String.format(Constants.EMULATOR_PRIMARY_ENDPOINT_FORMAT,
                        scheme,
                        host,
                        "10000"));
                URI queuePrimaryEndpoint = new URI(String.format(Constants.EMULATOR_PRIMARY_ENDPOINT_FORMAT,
                        scheme,
                        host,
                        "10001"));
                URI tablePrimaryEndpoint = new URI(String.format(Constants.EMULATOR_PRIMARY_ENDPOINT_FORMAT,
                        scheme,
                        host,
                        "10002"));

                URI blobSecondaryEndpoint = new URI(String.format(Constants.EMULATOR_SECONDRY_ENDPOINT_FORMAT,
                        scheme,
                        host,
                        "10000"));
                URI queueSecondaryEndpoint = new URI(String.format(Constants.EMULATOR_SECONDRY_ENDPOINT_FORMAT,
                        scheme,
                        host,
                        "10001"));
                URI tableSecondaryEndpoint = new URI(String.format(Constants.EMULATOR_SECONDRY_ENDPOINT_FORMAT,
                        scheme,
                        host,
                        "10002"));

                account = new StorageConnectionString(StorageAuthenticationSettings.forEmulator(),
                        new StorageEndpoint(blobPrimaryEndpoint, blobSecondaryEndpoint),
                        new StorageEndpoint(queuePrimaryEndpoint, queueSecondaryEndpoint),
                        new StorageEndpoint(tablePrimaryEndpoint, tableSecondaryEndpoint),
                        null,
                        null);
            } catch (URISyntaxException use) {
                throw logger.logExceptionAsError(new RuntimeException(use));
            }
            return account;
        } else {
            return null;
        }
    }

    private static StorageConnectionString tryCreateForService(final ConnectionSettings settings,
                                                               final ClientLogger logger) {
        ConnectionSettingsFilter automaticEndpointsMatchSpec = automaticEndpointsMatchSpec();
        ConnectionSettingsFilter explicitEndpointsMatchSpec = explicitEndpointsMatchSpec();

        boolean matchesAutomaticEndpointsSpec = ConnectionSettingsFilter.matchesSpecification(settings,
                automaticEndpointsMatchSpec);
        boolean matchesExplicitEndpointsSpec = ConnectionSettingsFilter.matchesSpecification(settings,
                explicitEndpointsMatchSpec);

        if (matchesAutomaticEndpointsSpec || matchesExplicitEndpointsSpec) {
            if (matchesAutomaticEndpointsSpec
                    && !settings.hasSetting(Constants.DEFAULT_ENDPOINTS_PROTOCOL_NAME)) {
                settings.setSetting(Constants.DEFAULT_ENDPOINTS_PROTOCOL_NAME, "https");
            }
            // If the settings BlobEndpoint, FileEndpoint, QueueEndpoint, TableEndpoint presents
            // it will be a well formed Uri, including 'http' or 'https' prefix.
            String blobEndpoint = settings.getSettingValue(Constants.BLOB_ENDPOINT_NAME);
            String queueEndpoint = settings.getSettingValue(Constants.QUEUE_ENDPOINT_NAME);
            String tableEndpoint = settings.getSettingValue(Constants.TABLE_ENDPOINT_NAME);
            String fileEndpoint = settings.getSettingValue(Constants.FILE_ENDPOINT_NAME);
            String blobSecondaryEndpoint = settings.getSettingValue(Constants.BLOB_SECONDARY_ENDPOINT_NAME);
            String queueSecondaryEndpoint = settings.getSettingValue(Constants.QUEUE_SECONDARY_ENDPOINT_NAME);
            String tableSecondaryEndpoint = settings.getSettingValue(Constants.TABLE_SECONDARY_ENDPOINT_NAME);
            String fileSecondaryEndpoint = settings.getSettingValue(Constants.FILE_SECONDARY_ENDPOINT_NAME);

            if (isValidPrimarySecondaryPair(blobEndpoint, blobSecondaryEndpoint)
                    && isValidPrimarySecondaryPair(queueEndpoint, queueSecondaryEndpoint)
                    && isValidPrimarySecondaryPair(tableEndpoint, tableSecondaryEndpoint)
                    && isValidPrimarySecondaryPair(fileEndpoint, fileSecondaryEndpoint)) {
                StorageConnectionString storageConnectionString = new StorageConnectionString(
                        StorageAuthenticationSettings.fromConnectionSettings(settings),
                        StorageEndpoint.fromStorageSettings(settings,
                                "blob",
                                Constants.BLOB_ENDPOINT_NAME,
                                Constants.BLOB_SECONDARY_ENDPOINT_NAME,
                                matchesAutomaticEndpointsSpec,
                                logger),
                        StorageEndpoint.fromStorageSettings(settings,
                                "queue",
                                Constants.QUEUE_ENDPOINT_NAME,
                                Constants.QUEUE_SECONDARY_ENDPOINT_NAME,
                                matchesAutomaticEndpointsSpec,
                                logger),
                        StorageEndpoint.fromStorageSettings(settings,
                                "table",
                                Constants.TABLE_ENDPOINT_NAME,
                                Constants.TABLE_SECONDARY_ENDPOINT_NAME,
                                matchesAutomaticEndpointsSpec,
                                logger),
                        StorageEndpoint.fromStorageSettings(settings,
                                "file",
                                Constants.FILE_ENDPOINT_NAME,
                                Constants.FILE_SECONDARY_ENDPOINT_NAME,
                                matchesAutomaticEndpointsSpec,
                                logger),
                        settings.getSettingValue(Constants.ACCOUNT_NAME_NAME));
                return storageConnectionString;
            }
        }
        return null;
    }

    private static ConnectionSettingsFilter automaticEndpointsMatchSpec() {
        return ConnectionSettingsFilter.matchesExactly(
                ConnectionSettingsFilter.matchesAll(
                        ConnectionSettingsFilter.matchesOne(
                                ConnectionSettingsFilter.matchesAll(requireAccountKey()),
                                requireSas()),
                        requireAccountName(),
                        optionalEndpoints(),
                        optionalEndpointProtocolAndSuffix())
        );
    }

    private static ConnectionSettingsFilter explicitEndpointsMatchSpec() {
        ConnectionSettingsFilter validCredentials = ConnectionSettingsFilter.matchesOne(
                requireAccountNameAndKeyNoSas(),
                requireSasOptionalAccountNameNoAccountKey(),
                noAccountNameNoAccountKeyNoSas());

        return ConnectionSettingsFilter.matchesExactly(ConnectionSettingsFilter.matchesAll(validCredentials,
                requireAtLeastOnePrimaryEndpoint(),
                optionalSecondaryEndpoints()));
    }

    private static ConnectionSettingsFilter requireAccountName() {
        return ConnectionSettingsFilter.allRequired(Constants.ACCOUNT_NAME_NAME);
    }

    private static ConnectionSettingsFilter requireAccountKey() {
        return ConnectionSettingsFilter.allRequired(Constants.ACCOUNT_KEY_NAME);
    }

    private static ConnectionSettingsFilter requireSas() {
        return ConnectionSettingsFilter.allRequired(Constants.SHARED_ACCESS_SIGNATURE_NAME);
    }

    private static ConnectionSettingsFilter requireAccountNameAndKey() {
        return ConnectionSettingsFilter.allRequired(Constants.ACCOUNT_NAME_NAME, Constants.ACCOUNT_KEY_NAME);
    }

    private static ConnectionSettingsFilter optionalAccountName() {
        return ConnectionSettingsFilter.optional(Constants.ACCOUNT_NAME_NAME);
    }

    private static ConnectionSettingsFilter noAccountKey() {
        return ConnectionSettingsFilter.none(Constants.ACCOUNT_KEY_NAME);
    }

    private static ConnectionSettingsFilter noSas() {
        return ConnectionSettingsFilter.none(Constants.SHARED_ACCESS_SIGNATURE_NAME);
    }

    private static ConnectionSettingsFilter requireAccountNameAndKeyNoSas() {
        return ConnectionSettingsFilter.matchesAll(
                requireAccountNameAndKey(),
                noSas());
    }

    private static ConnectionSettingsFilter requireSasOptionalAccountNameNoAccountKey() {
        return ConnectionSettingsFilter.matchesAll(
                requireSas(),
                optionalAccountName(),
                noAccountKey());
    }

    private static ConnectionSettingsFilter noAccountNameNoAccountKeyNoSas() {
        return ConnectionSettingsFilter.none(Constants.ACCOUNT_NAME_NAME,
                Constants.ACCOUNT_KEY_NAME, Constants.SHARED_ACCESS_SIGNATURE_NAME);
    }

    private static ConnectionSettingsFilter optionalEndpointProtocolAndSuffix() {
        return ConnectionSettingsFilter.optional(Constants.DEFAULT_ENDPOINTS_PROTOCOL_NAME,
                Constants.ENDPOINT_SUFFIX_NAME);
    }

    private static ConnectionSettingsFilter optionalEndpoints() {
        return ConnectionSettingsFilter.optional(
                Constants.BLOB_ENDPOINT_NAME, Constants.BLOB_SECONDARY_ENDPOINT_NAME,
                Constants.QUEUE_ENDPOINT_NAME, Constants.QUEUE_SECONDARY_ENDPOINT_NAME,
                Constants.TABLE_ENDPOINT_NAME, Constants.TABLE_SECONDARY_ENDPOINT_NAME,
                Constants.FILE_ENDPOINT_NAME, Constants.FILE_SECONDARY_ENDPOINT_NAME);
    }

    private static ConnectionSettingsFilter requireAtLeastOnePrimaryEndpoint() {
        return ConnectionSettingsFilter.atLeastOne(Constants.BLOB_ENDPOINT_NAME,
                Constants.QUEUE_ENDPOINT_NAME,
                Constants.TABLE_ENDPOINT_NAME,
                Constants.FILE_ENDPOINT_NAME);
    }

    private static ConnectionSettingsFilter optionalSecondaryEndpoints() {
        return ConnectionSettingsFilter.optional(Constants.BLOB_SECONDARY_ENDPOINT_NAME,
                Constants.QUEUE_SECONDARY_ENDPOINT_NAME,
                Constants.TABLE_SECONDARY_ENDPOINT_NAME,
                Constants.FILE_SECONDARY_ENDPOINT_NAME);
    }

    private StorageConnectionString(final StorageAuthenticationSettings storageAuthSettings,
                                    final StorageEndpoint blobEndpoint,
                                    final StorageEndpoint queueEndpoint,
                                    final StorageEndpoint tableEndpoint,
                                    final StorageEndpoint fileEndpoint,
                                    final String accountName) {
        this.storageAuthSettings = storageAuthSettings;
        this.blobEndpoint = blobEndpoint;
        this.fileEndpoint = fileEndpoint;
        this.queueEndpoint = queueEndpoint;
        this.tableEndpoint = tableEndpoint;
        this.accountName = accountName;
    }

    private static Boolean isValidPrimarySecondaryPair(String primary, String secondary) {
        if (primary != null) {
            return true;
        }
        if (primary == null && secondary == null) {
            return true;
        }
        return false;
    }

    private static boolean isNullOrEmpty(final String value) {
        return value == null || value.length() == 0;
    }

    @FunctionalInterface
    public interface ConnectionSettingsFilter {
        ConnectionSettings apply(ConnectionSettings inputSettings);

        static ConnectionSettingsFilter allRequired(final String... settingNames) {
            return (ConnectionSettings inputSettings) -> {
                ConnectionSettings outputSettings = inputSettings.clone();
                for (final String settingName : settingNames) {
                    if (outputSettings.hasSetting(settingName)) {
                        outputSettings.removeSetting(settingName);
                    } else {
                        return null;
                    }
                }
                return outputSettings;
            };
        }

        static ConnectionSettingsFilter optional(final String... settingNames) {
            return (ConnectionSettings inputSettings) -> {
                ConnectionSettings outputSettings = inputSettings.clone();
                for (final String settingName : settingNames) {
                    if (outputSettings.hasSetting(settingName)) {
                        outputSettings.removeSetting(settingName);
                    }
                }
                return outputSettings;
            };
        }

        static ConnectionSettingsFilter atLeastOne(final String... settingNames) {
            return (ConnectionSettings inputSettings) -> {
                ConnectionSettings outputSettings = inputSettings.clone();
                boolean foundOne = false;
                for (final String settingName : settingNames) {
                    if (outputSettings.hasSetting(settingName)) {
                        outputSettings.removeSetting(settingName);
                        foundOne = true;
                    }
                }
                return foundOne ? outputSettings : null;
            };
        }

        static ConnectionSettingsFilter none(final String... settingNames) {
            return (ConnectionSettings inputSettings) -> {
                ConnectionSettings outputSettings = inputSettings.clone();
                boolean foundOne = false;
                for (final String settingName : settingNames) {
                    if (outputSettings.hasSetting(settingName)) {
                        outputSettings.removeSetting(settingName);
                        foundOne = true;
                    }
                }
                return foundOne ? null : outputSettings;
            };
        }

        static ConnectionSettingsFilter matchesAll(final ConnectionSettingsFilter... filters) {
            return (ConnectionSettings inputSettings) -> {
                ConnectionSettings outputSettings = inputSettings.clone();
                for (final ConnectionSettingsFilter filter : filters) {
                    if (outputSettings == null) {
                        break;
                    }
                    outputSettings = filter.apply(outputSettings);
                }
                return outputSettings;
            };
        }

        static ConnectionSettingsFilter matchesOne(final ConnectionSettingsFilter... filters) {
            return (ConnectionSettings settings) -> {
                ConnectionSettings matchResult = null;
                for (final ConnectionSettingsFilter filter : filters) {
                    ConnectionSettings result = filter.apply(settings.clone());
                    if (result != null) {
                        if (matchResult == null) {
                            matchResult = result;
                        } else {
                            return null;
                        }
                    }
                }
                return matchResult;
            };
        }

        static ConnectionSettingsFilter matchesExactly(final ConnectionSettingsFilter filter) {
            return (ConnectionSettings settings) -> {
                ConnectionSettings result = settings.clone();
                result = filter.apply(result);
                if (result == null || !result.isEmpty()) {
                    return null;
                } else {
                    return  result;
                }
            };
        }

        static boolean matchesSpecification(ConnectionSettings settings,
                                                    ConnectionSettingsFilter... constraints) {
            for (ConnectionSettingsFilter constraint: constraints) {
                ConnectionSettings remainingSettings = constraint.apply(settings);
                if (remainingSettings == null) {
                    return false;
                } else {
                    settings = remainingSettings;
                }
            }
            if (settings.isEmpty()) {
                return true;
            }
            return false;
        }
    }

    /**
     * A dictionary representation of all settings in a connection string.
     */
    public static class ConnectionSettings {
        private final Map<String, String> settings;

        public boolean hasSetting(String name) {
            return this.settings.containsKey(name);
        }

        public void removeSetting(String name) {
            this.settings.remove(name);
        }

        public String getSettingValue(String name) {
            return this.settings.get(name);
        }

        public boolean isEmpty() {
            return this.settings.isEmpty();
        }

        public void setSetting(String name, String value) {
            this.settings.put(name, value);
        }

        public static ConnectionSettings fromConnectionString(final String connString,
                                                               final ClientLogger logger) {
            HashMap<String, String> map = new HashMap<>();
            final String[] settings = connString.split(";");
            for (int i = 0; i < settings.length; i++) {
                String setting = settings[i].trim();
                if (setting.length() > 0) {
                    final int idx = setting.indexOf("=");
                    if (idx == -1
                            || idx == 0
                            || idx == settings[i].length() - 1) {
                        // handle no_equal_symbol, "=Bar", "Foo="
                        throw logger.logExceptionAsError(new IllegalArgumentException("Invalid connection string."));
                    }
                    map.put(setting.substring(0, idx), setting.substring(idx + 1));
                }
            }
            return new ConnectionSettings(map);
        }

        @Override
        public ConnectionSettings clone() {
            return new ConnectionSettings(new HashMap<>(this.settings));
        }

        private ConnectionSettings(Map<String, String> settings) {
            this.settings = settings;
        }
    }

    /**
     * Type to hold required settings for accessing a storage account.
     */
    public static class StorageAuthenticationSettings {
        private final Type type;
        private final String sasToken;
        private final Account account;

        /**
         * @return the settings type (None, Account Name and Key, Sas token).
         */
        public Type getType() {
            return this.type;
        }

        /**
         * @return the sas token.
         */
        public String getSasToken() {
            return this.sasToken;
        }

        /**
         * @return the account instance containing account name and key
         */
        public Account getAccount() {
            return this.account;
        }

        /**
         * Creates {@link StorageAuthenticationSettings} from the given connection settings.
         *
         * @param settings the connection settings.
         * @return the StorageAuthenticationSettings.
         */
        public static StorageAuthenticationSettings fromConnectionSettings(final ConnectionSettings settings) {
            final String accountName = settings.getSettingValue(Constants.ACCOUNT_NAME_NAME);
            final String accountKey = settings.getSettingValue(Constants.ACCOUNT_KEY_NAME);
            final String sasSignature = settings.getSettingValue(Constants.SHARED_ACCESS_SIGNATURE_NAME);

            if (accountName != null && accountKey != null && sasSignature == null) {
                return new StorageAuthenticationSettings(new Account(accountName, accountKey));
            }
            if (accountKey == null && sasSignature != null) {
                return new StorageAuthenticationSettings(sasSignature);
            }
            return new StorageAuthenticationSettings();
        }

        /**
         * @return get a {@link StorageAuthenticationSettings} for emulator.
         */
        public static StorageAuthenticationSettings forEmulator() {
            return new StorageAuthenticationSettings(new Account(Constants.DEVSTORE_ACCOUNT_NAME,
                    Constants.DEVSTORE_ACCOUNT_KEY));
        }

        /**
         * Creates default {@link StorageAuthenticationSettings} indicating absence of authentication
         * setting.
         */
        private StorageAuthenticationSettings() {
            this.type = Type.NONE;
            this.account = null;
            this.sasToken = null;
        }

        /**
         * Creates {@link StorageAuthenticationSettings} indicating Sas token based authentication
         * settings.
         *
         * @param sasToken the sas token
         */
        private StorageAuthenticationSettings(String sasToken) {
            this.type = Type.SAS_TOKEN;
            this.sasToken = Objects.requireNonNull(sasToken);
            this.account = null;
        }

        /**
         * Creates {@link StorageAuthenticationSettings} indicating account name and key based
         * authentication settings.
         *
         * @param account the account instance holding account name and key
         */
        private StorageAuthenticationSettings(Account account) {
            this.type = Type.ACCOUNT_NAME_KEY;
            this.account = Objects.requireNonNull(account);
            this.sasToken = null;
        }

        /**
         * Authentication settings type.
         */
        public enum Type {
            NONE(),
            ACCOUNT_NAME_KEY(),
            SAS_TOKEN(),
        }

        /**
         * Type to hold storage account name and access key.
         */
        public static class Account {
            private String name;
            private String accessKey;

            /**
             * Creates Account.
             *
             * @param name the account name
             * @param accessKey the access key
             */
            Account(String name, String accessKey) {
                this.name = Objects.requireNonNull(name);
                this.accessKey = Objects.requireNonNull(accessKey);
            }

            /**
             * @return the account key
             */
            public String getName() {
                return this.name;
            }

            /**
             * @return the account access key
             */
            public String getAccessKey() {
                return this.accessKey;
            }
        }
    }

    /**
     * Type representing storage service (blob, queue, table, file) endpoint.
     */
    public static class StorageEndpoint {
        private final URI primaryUri;
        private final URI secondaryUri;

        /**
         * Creates {@link StorageEndpoint} with primary endpoint.
         *
         * @param primaryUri the primary endpoint URI
         */
        public StorageEndpoint(URI primaryUri) {
            this.primaryUri = Objects.requireNonNull(primaryUri);
            this.secondaryUri = null;
        }

        /**
         * Creates {@link StorageEndpoint} with primary and secondary endpoint.
         * @param primaryUri the primary endpoint URI
         * @param secondaryUri the secondary endpoint URI
         */
        public StorageEndpoint(URI primaryUri, URI secondaryUri) {
            this.primaryUri = Objects.requireNonNull(primaryUri);
            this.secondaryUri = Objects.requireNonNull(secondaryUri);
        }

        /**
         * @return the primary endpoint URI
         */
        public URI getPrimaryUri() {
            return this.primaryUri;
        }

        /**
         * @return the secondary endpoint URI
         */
        public URI getSecondaryUri() {
            return this.secondaryUri;
        }

        /**
         * Creates a StorageEndpoint from the given connection settings.
         *
         * @param settings the settings derived from storage connection string.
         * @param service the storage service. Possible values are blob, queue, table and file.
         * @param serviceEndpointName the name of the entry in the settings representing
         *                            a well formed primary URI to the service. Possible values are
         *                            BlobEndpoint, QueueEndpoint, FileEndpoint and TableEndpoint.
         *
         * @param serviceSecondaryEndpointName the name of the entry in the settings representing
         *                                     a well formed secondary URI to the service. Possible
         *                                     values are BlobSecondaryEndpoint, QueueSecondaryEndpoint
         *                                     FileSecondaryEndpoint and TableSecondaryEndpoint.
         * @param matchesAutomaticEndpointsSpec true indicate that the settings has entries from which
         *                                      endpoint to the service can be build. Possible values
         *                                      are DefaultEndpointsProtocol, AccountName, AccountKey
         *                                      and EndpointSuffix.
         * @param logger the logger to log any exception while processing the settings.
         * @return a StorageEndpoint if required settings exists, null otherwise.
         */
        public static StorageEndpoint fromStorageSettings(final ConnectionSettings settings,
                                                          final String service,
                                                          final String serviceEndpointName,
                                                          final String serviceSecondaryEndpointName,
                                                          final Boolean matchesAutomaticEndpointsSpec,
                                                          final ClientLogger logger) {
            String serviceEndpoint = settings.getSettingValue(serviceEndpointName);
            String serviceSecondaryEndpoint = settings.getSettingValue(serviceSecondaryEndpointName);

            if (serviceEndpoint != null && serviceSecondaryEndpoint != null) {
                try {
                    return new StorageEndpoint(new URI(serviceEndpoint), new URI(serviceSecondaryEndpoint));
                } catch (URISyntaxException use) {
                    throw logger.logExceptionAsError(new RuntimeException(use));
                }
            }

            if (serviceEndpoint != null) {
                try {
                    return new StorageEndpoint(new URI(serviceEndpoint));
                } catch (URISyntaxException use) {
                    throw logger.logExceptionAsError(new RuntimeException(use));
                }
            }

            if (matchesAutomaticEndpointsSpec) {
                // Derive URI from relevant settings
                //
                final String protocol = settings.getSettingValue(Constants.DEFAULT_ENDPOINTS_PROTOCOL_NAME);
                if (isNullOrEmpty(protocol)) {
                    throw logger.logExceptionAsError(new IllegalArgumentException("'DefaultEndpointsProtocol' is " +
                            "required, specify whether to use http or https."));
                }
                final String accountName = settings.getSettingValue(Constants.ACCOUNT_NAME_NAME);
                if (isNullOrEmpty(accountName)) {
                    throw logger.logExceptionAsError(new IllegalArgumentException("'AccountName' is required."));
                }
                //
                String endpointSuffix = settings.getSettingValue(Constants.ENDPOINT_SUFFIX_NAME);
                if (endpointSuffix == null) {
                    // default: core.windows.net
                    endpointSuffix = Constants.DEFAULT_DNS;
                }
                final URI primaryUri;
                final URI secondaryUri;
                try {
                    primaryUri = new URI(String.format("%s://%s.%s.%s",
                            protocol,
                            accountName,
                            service,
                            endpointSuffix));
                } catch (URISyntaxException use) {
                    throw logger.logExceptionAsError(new RuntimeException(use));
                }

                try {
                    secondaryUri = new URI(String.format("%s://%s-secondary.%s.%s",
                            protocol,
                            accountName,
                            service,
                            endpointSuffix));
                } catch (URISyntaxException use) {
                    throw logger.logExceptionAsError(new RuntimeException(use));
                }
                return new StorageEndpoint(primaryUri, secondaryUri);
            }
            return null;
        }
    }

    private static class Constants {
        /**
         * The setting name for the storage account name.
         */
        private static final String ACCOUNT_NAME_NAME = "AccountName";

        /**
         * The setting name for the storage account key.
         */
        private static final String ACCOUNT_KEY_NAME = "AccountKey";

        /**
         * The setting name for using the default storage endpoints with the specified protocol.
         */
        private static final String DEFAULT_ENDPOINTS_PROTOCOL_NAME = "DefaultEndpointsProtocol";

        /**
         * The setting name for a custom blob storage endpoint.
         */
        private static final String BLOB_ENDPOINT_NAME = "BlobEndpoint";

        /**
         * The setting name for a custom blob storage secondary endpoint.
         */
        private static final String BLOB_SECONDARY_ENDPOINT_NAME = "BlobSecondaryEndpoint";

        /**
         * The setting name for a custom queue endpoint.
         */
        private static final String QUEUE_ENDPOINT_NAME = "QueueEndpoint";

        /**
         * The setting name for a custom queue secondary endpoint.
         */
        private static final String QUEUE_SECONDARY_ENDPOINT_NAME = "QueueSecondaryEndpoint";

        /**
         * The setting name for a custom file endpoint.
         */
        private static final String FILE_ENDPOINT_NAME = "FileEndpoint";

        /**
         * The setting name for a custom file secondary endpoint.
         */
        private static final String FILE_SECONDARY_ENDPOINT_NAME = "FileSecondaryEndpoint";

        /**
         * The setting name for a custom table storage endpoint.
         */
        private static final String TABLE_ENDPOINT_NAME = "TableEndpoint";

        /**
         * The setting name for a custom table storage secondary endpoint.
         */
        private static final String TABLE_SECONDARY_ENDPOINT_NAME = "TableSecondaryEndpoint";

        /**
         * The setting name for a custom storage endpoint suffix.
         */
        private static final String ENDPOINT_SUFFIX_NAME = "EndpointSuffix";

        /**
         * The setting name for a shared access key.
         */
        private static final String SHARED_ACCESS_SIGNATURE_NAME = "SharedAccessSignature";

        /**
         * The setting name for using the emulator storage.
         */
        private static final String USE_EMULATOR_STORAGE_NAME = "UseDevelopmentStorage";

        /**
         * The setting name for specifying a development storage proxy Uri.
         */
        private static final String EMULATOR_STORAGE_PROXY_URI_NAME = "DevelopmentStorageProxyUri";

        /**
         * The root storage DNS name.
         */
        private static final String DEFAULT_DNS = "core.windows.net";

        /**
         * The format string for the primary endpoint in emulator.
         */
        private static final String EMULATOR_PRIMARY_ENDPOINT_FORMAT = "%s://%s:%s/devstoreaccount1";

        /**
         * The format string for the secondary endpoint in emulator.
         */
        private static final String EMULATOR_SECONDRY_ENDPOINT_FORMAT = "%s://%s:%s/devstoreaccount1-secondary";

        /**
         * The default account key for the development storage.
         */
        private static final String DEVSTORE_ACCOUNT_KEY
                = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

        /**
         * The default account name for the development storage.
         */
        private static final String DEVSTORE_ACCOUNT_NAME = "devstoreaccount1";
    }
}