// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** The EmailHookParameterPatch model. */
@Fluent
public final class EmailNotificationHookParameterPatch {
    /*
     * Email TO: list.
     */
    @JsonProperty(value = "toList")
    private Option<List<String>> toList;

    /**
     * Set the toList property: Email TO: list.
     *
     * @param toList the toList value to set.
     * @return the EmailHookParameterPatch object itself.
     */
    public EmailNotificationHookParameterPatch setEmailsToAlert(List<String> toList) {
        if (toList == null) {
            this.toList = Option.empty();
        } else {
            this.toList = Option.of(toList);
        }
        return this;
    }
}
