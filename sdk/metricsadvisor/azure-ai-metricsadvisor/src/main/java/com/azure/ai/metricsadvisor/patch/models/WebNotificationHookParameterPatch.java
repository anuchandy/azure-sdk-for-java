// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/** The WebhookHookParameterPatch model. */
@Fluent
public final class WebNotificationHookParameterPatch {
    /*
     * API address, will be called when alert is triggered, only support POST
     * method via SSL
     */
    @JsonProperty(value = "endpoint")
    private Option<String> endpoint;

    /*
     * (Deprecated) The username, if using basic authentication
     */
    @JsonProperty(value = "username")
    private Option<String> username;

    /*
     * (Deprecated) The password, if using basic authentication
     */
    @JsonProperty(value = "password")
    private Option<String> password;

    /*
     * custom headers in api call
     */
    @JsonProperty(value = "headers")
    private Option<Map<String, String>> headers;

    /*
     * The certificate key, if using client certificate
     */
    @JsonProperty(value = "certificateKey")
    private Option<String> certificateKey;

    /*
     * The certificate password, if using client certificate
     */
    @JsonProperty(value = "certificatePassword")
    private Option<String> certificatePassword;

    /**
     * Set the endpoint property: API address, will be called when alert is triggered, only support POST method via SSL.
     *
     * @param endpoint the endpoint value to set.
     * @return the WebhookHookParameterPatch object itself.
     */
    public WebNotificationHookParameterPatch setEndpoint(String endpoint) {
        if (endpoint == null) {
            this.endpoint = Option.empty();
        } else {
            this.endpoint = Option.of(endpoint);
        }
        return this;
    }

    /**
     * Set the username property: (Deprecated) The username, if using basic authentication.
     *
     * @param username the username value to set.
     * @return the WebhookHookParameterPatch object itself.
     */
    public WebNotificationHookParameterPatch setUsername(String username) {
        if (username == null) {
            this.username = Option.empty();
        } else {
            this.username = Option.of(username);
        }
        return this;
    }

    /**
     * Set the password property: (Deprecated) The password, if using basic authentication.
     *
     * @param password the password value to set.
     * @return the WebhookHookParameterPatch object itself.
     */
    public WebNotificationHookParameterPatch setPassword(String password) {
        if (password == null) {
            this.password = Option.empty();
        } else {
            this.password = Option.of(password);
        }
        return this;
    }

    /**
     * Set the headers property: custom headers in api call.
     *
     * @param headers the headers value to set.
     * @return the WebhookHookParameterPatch object itself.
     */
    public WebNotificationHookParameterPatch setHeaders(Map<String, String> headers) {
        if (headers == null) {
            this.headers = Option.empty();
        } else {
            this.headers = Option.of(headers);
        }
        return this;
    }

    /**
     * Set the certificateKey property: The certificate key, if using client certificate.
     *
     * @param certificateKey the certificateKey value to set.
     * @return the WebhookHookParameterPatch object itself.
     */
    public WebNotificationHookParameterPatch setCertificateKey(String certificateKey) {
        if (certificateKey == null) {
            this.certificateKey = Option.empty();
        } else {
            this.certificateKey = Option.of(certificateKey);
        }
        return this;
    }

    /**
     * Set the certificatePassword property: The certificate password, if using client certificate.
     *
     * @param certificatePassword the certificatePassword value to set.
     * @return the WebhookHookParameterPatch object itself.
     */
    public WebNotificationHookParameterPatch setCertificatePassword(String certificatePassword) {
        if (certificatePassword == null) {
            this.certificatePassword = Option.empty();
        } else {
            this.certificatePassword = Option.of(certificatePassword);
        }
        return this;
    }
}
