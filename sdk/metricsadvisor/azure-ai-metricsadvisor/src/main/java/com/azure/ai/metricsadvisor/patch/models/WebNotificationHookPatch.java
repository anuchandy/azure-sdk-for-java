// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** The WebhookHookInfoPatch model. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "hookType")
@JsonTypeName("Webhook")
@Fluent
public final class WebNotificationHookPatch extends NotificationHookPatch {
    /*
     * The hookParameter property.
     */
    @JsonProperty(value = "hookParameter")
    private Option<WebNotificationHookParameterPatch> hookParameter;

    /**
     * Set the hookParameter property: The hookParameter property.
     *
     * @param hookParameter the hookParameter value to set.
     * @return the WebhookHookInfoPatch object itself.
     */
    public WebNotificationHookPatch setHookParameter(WebNotificationHookParameterPatch hookParameter) {
        if (hookParameter == null) {
            this.hookParameter = Option.empty();
        } else {
            this.hookParameter = Option.of(hookParameter);
        }
        return this;
    }
}
