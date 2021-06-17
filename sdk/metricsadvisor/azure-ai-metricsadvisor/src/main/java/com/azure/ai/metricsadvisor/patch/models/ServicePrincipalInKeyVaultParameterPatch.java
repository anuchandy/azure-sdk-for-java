// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The ServicePrincipalInKVParamPatch model. */
@Fluent
public final class ServicePrincipalInKeyVaultParameterPatch {
    /*
     * The Key Vault endpoint that storing the service principal.
     */
    @JsonProperty(value = "keyVaultEndpoint")
    private Option<String> keyVaultEndpoint;

    /*
     * The Client Id to access the Key Vault.
     */
    @JsonProperty(value = "keyVaultClientId")
    private Option<String> keyVaultClientId;

    /*
     * The Client Secret to access the Key Vault.
     */
    @JsonProperty(value = "keyVaultClientSecret")
    private Option<String> keyVaultClientSecret;

    /*
     * The secret name of the service principal's client Id in the Key Vault.
     */
    @JsonProperty(value = "servicePrincipalIdNameInKV")
    private Option<String> servicePrincipalIdNameInKV;

    /*
     * The secret name of the service principal's client secret in the Key
     * Vault.
     */
    @JsonProperty(value = "servicePrincipalSecretNameInKV")
    private Option<String> servicePrincipalSecretNameInKV;

    /*
     * The tenant id of your service principal.
     */
    @JsonProperty(value = "tenantId")
    private Option<String> tenantId;

    /**
     * Set the keyVaultEndpoint property: The Key Vault endpoint that storing the service principal.
     *
     * @param keyVaultEndpoint the keyVaultEndpoint value to set.
     * @return the ServicePrincipalInKVParamPatch object itself.
     */
    public ServicePrincipalInKeyVaultParameterPatch setKeyVaultEndpoint(String keyVaultEndpoint) {
        if (keyVaultEndpoint == null) {
            this.keyVaultEndpoint = Option.empty();
        } else {
            this.keyVaultEndpoint = Option.of(keyVaultEndpoint);
        }
        return this;
    }

    /**
     * Set the keyVaultClientId property: The Client Id to access the Key Vault.
     *
     * @param keyVaultClientId the keyVaultClientId value to set.
     * @return the ServicePrincipalInKVParamPatch object itself.
     */
    public ServicePrincipalInKeyVaultParameterPatch setKeyVaultClientId(String keyVaultClientId) {
        if (keyVaultClientId == null) {
            this.keyVaultClientId = Option.empty();
        } else {
            this.keyVaultClientId = Option.of(keyVaultClientId);
        }
        return this;
    }

    /**
     * Set the keyVaultClientSecret property: The Client Secret to access the Key Vault.
     *
     * @param keyVaultClientSecret the keyVaultClientSecret value to set.
     * @return the ServicePrincipalInKVParamPatch object itself.
     */
    public ServicePrincipalInKeyVaultParameterPatch setKeyVaultClientSecret(String keyVaultClientSecret) {
        if (keyVaultClientSecret == null) {
            this.keyVaultClientSecret = Option.empty();
        } else {
            this.keyVaultClientSecret = Option.of(keyVaultClientSecret);
        }
        return this;
    }

    /**
     * Set the servicePrincipalIdNameInKV property: The secret name of the service principal's client Id in the Key
     * Vault.
     *
     * @param servicePrincipalIdNameInKV the servicePrincipalIdNameInKV value to set.
     * @return the ServicePrincipalInKVParamPatch object itself.
     */
    public ServicePrincipalInKeyVaultParameterPatch setServicePrincipalIdNameInKV(String servicePrincipalIdNameInKV) {
        if (servicePrincipalIdNameInKV == null) {
            this.servicePrincipalIdNameInKV = Option.empty();
        } else {
            this.servicePrincipalIdNameInKV = Option.of(servicePrincipalIdNameInKV);
        }
        return this;
    }

    /**
     * Set the servicePrincipalSecretNameInKV property: The secret name of the service principal's client secret in the
     * Key Vault.
     *
     * @param servicePrincipalSecretNameInKV the servicePrincipalSecretNameInKV value to set.
     * @return the ServicePrincipalInKVParamPatch object itself.
     */
    public ServicePrincipalInKeyVaultParameterPatch setServicePrincipalSecretNameInKV(String servicePrincipalSecretNameInKV) {
        if (servicePrincipalSecretNameInKV == null) {
            this.servicePrincipalSecretNameInKV = Option.empty();
        } else {
            this.servicePrincipalSecretNameInKV = Option.of(servicePrincipalSecretNameInKV);
        }
        return this;
    }

    /**
     * Set the tenantId property: The tenant id of your service principal.
     *
     * @param tenantId the tenantId value to set.
     * @return the ServicePrincipalInKVParamPatch object itself.
     */
    public ServicePrincipalInKeyVaultParameterPatch setTenantId(String tenantId) {
        if (tenantId == null) {
            this.tenantId = Option.empty();
        } else {
            this.tenantId = Option.of(tenantId);
        }
        return this;
    }
}
