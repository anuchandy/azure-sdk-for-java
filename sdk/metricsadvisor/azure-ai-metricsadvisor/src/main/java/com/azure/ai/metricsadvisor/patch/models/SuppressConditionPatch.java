// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The SuppressConditionPatch model. */
@Fluent
public final class SuppressConditionPatch {
    /*
     * min point number, value range : [1, +∞)
     */
    @JsonProperty(value = "minNumber")
    private Option<Integer> minNumber;

    /*
     * min point ratio, value range : (0, 100]
     */
    @JsonProperty(value = "minRatio")
    private Option<Double> minRatio;


    /**
     * Set the minNumber property: min point number, value range : [1, +∞).
     *
     * @param minNumber the minNumber value to set.
     * @return the SuppressConditionPatch object itself.
     */
    public SuppressConditionPatch setMinNumber(Integer minNumber) {
        if (minNumber == null) {
            this.minNumber = Option.empty();
        } else {
            this.minNumber = Option.of(minNumber);
        }
        return this;
    }

    /**
     * Set the minRatio property: min point ratio, value range : (0, 100].
     *
     * @param minRatio the minRatio value to set.
     * @return the SuppressConditionPatch object itself.
     */
    public SuppressConditionPatch setMinRatio(Double minRatio) {
        if (minRatio == null) {
            this.minRatio = Option.empty();
        } else {
            this.minRatio = Option.of(minRatio);
        }
        return this;
    }
}
