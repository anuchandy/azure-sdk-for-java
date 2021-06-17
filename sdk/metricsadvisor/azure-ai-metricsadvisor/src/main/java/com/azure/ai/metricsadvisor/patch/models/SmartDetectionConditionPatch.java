// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.ai.metricsadvisor.models.AnomalyDetectorDirection;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The SmartDetectionConditionPatch model. */
@Fluent
public final class SmartDetectionConditionPatch {
    /*
     * sensitivity, value range : (0, 100]
     */
    @JsonProperty(value = "sensitivity")
    private  Option<Double> sensitivity;

    /*
     * detection direction
     */
    @JsonProperty(value = "anomalyDetectorDirection")
    private Option<AnomalyDetectorDirection> anomalyDetectorDirection;

    /*
     * The suppressCondition property.
     */
    @JsonProperty(value = "suppressCondition")
    private  Option<SuppressConditionPatch> suppressCondition;

    /**
     * Set the sensitivity property: sensitivity, value range : (0, 100].
     *
     * @param sensitivity the sensitivity value to set.
     * @return the SmartDetectionConditionPatch object itself.
     */
    public SmartDetectionConditionPatch setSensitivity(Double sensitivity) {
        if (sensitivity == null) {
            this.sensitivity = Option.empty();
        } else {
            this.sensitivity = Option.of(sensitivity);
        }
        return this;
    }

    /**
     * Set the anomalyDetectorDirection property: detection direction.
     *
     * @param anomalyDetectorDirection the anomalyDetectorDirection value to set.
     * @return the SmartDetectionConditionPatch object itself.
     */
    public SmartDetectionConditionPatch setAnomalyDetectorDirection(AnomalyDetectorDirection anomalyDetectorDirection) {
        if (anomalyDetectorDirection == null) {
            this.anomalyDetectorDirection = Option.empty();
        } else {
            this.anomalyDetectorDirection = Option.of(anomalyDetectorDirection);
        }
        return this;
    }

    /**
     * Set the suppressCondition property: The suppressCondition property.
     *
     * @param suppressCondition the suppressCondition value to set.
     * @return the SmartDetectionConditionPatch object itself.
     */
    public SmartDetectionConditionPatch setSuppressCondition(SuppressConditionPatch suppressCondition) {
        if (suppressCondition == null) {
            this.suppressCondition = Option.empty();
        } else {
            this.suppressCondition = Option.of(suppressCondition);
        }
        return this;
    }
}
