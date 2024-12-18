// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.providerhub.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The FeaturesRule model. */
@Fluent
public class FeaturesRule {
    /*
     * The requiredFeaturesPolicy property.
     */
    @JsonProperty(value = "requiredFeaturesPolicy", required = true)
    private FeaturesPolicy requiredFeaturesPolicy;

    /** Creates an instance of FeaturesRule class. */
    public FeaturesRule() {
    }

    /**
     * Get the requiredFeaturesPolicy property: The requiredFeaturesPolicy property.
     *
     * @return the requiredFeaturesPolicy value.
     */
    public FeaturesPolicy requiredFeaturesPolicy() {
        return this.requiredFeaturesPolicy;
    }

    /**
     * Set the requiredFeaturesPolicy property: The requiredFeaturesPolicy property.
     *
     * @param requiredFeaturesPolicy the requiredFeaturesPolicy value to set.
     * @return the FeaturesRule object itself.
     */
    public FeaturesRule withRequiredFeaturesPolicy(FeaturesPolicy requiredFeaturesPolicy) {
        this.requiredFeaturesPolicy = requiredFeaturesPolicy;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (requiredFeaturesPolicy() == null) {
            throw LOGGER.logExceptionAsError(
                new IllegalArgumentException("Missing required property requiredFeaturesPolicy in model FeaturesRule"));
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(FeaturesRule.class);
}
