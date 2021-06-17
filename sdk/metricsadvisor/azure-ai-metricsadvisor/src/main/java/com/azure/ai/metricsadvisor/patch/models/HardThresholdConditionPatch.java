// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.ai.metricsadvisor.models.AnomalyDetectorDirection;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The HardThresholdConditionPatch model. */
@Fluent
public final class HardThresholdConditionPatch {
    /*
     * lower bound
     *
     * should be specified when anomalyDetectorDirection is Both or Down
     */
    @JsonProperty(value = "lowerBound")
    private Option<Double> lowerBound;

    /*
     * upper bound
     *
     * should be specified when anomalyDetectorDirection is Both or Up
     */
    @JsonProperty(value = "upperBound")
    private Option<Double> upperBound;

    /*
     * detection direction
     */
    @JsonProperty(value = "anomalyDetectorDirection")
    private Option<AnomalyDetectorDirection> anomalyDetectorDirection;

    /*
     * The suppressCondition property.
     */
    @JsonProperty(value = "suppressCondition")
    private Option<SuppressConditionPatch> suppressCondition;

    /**
     * Set the lowerBound property: lower bound
     *
     * <p>should be specified when anomalyDetectorDirection is Both or Down.
     *
     * @param lowerBound the lowerBound value to set.
     * @return the HardThresholdConditionPatch object itself.
     */
    public HardThresholdConditionPatch setLowerBound(Double lowerBound) {
        if (lowerBound == null) {
            this.lowerBound = Option.empty();
        } else {
            this.lowerBound = Option.of(lowerBound);
        }
        return this;
    }

    /**
     * Set the upperBound property: upper bound
     *
     * <p>should be specified when anomalyDetectorDirection is Both or Up.
     *
     * @param upperBound the upperBound value to set.
     * @return the HardThresholdConditionPatch object itself.
     */
    public HardThresholdConditionPatch setUpperBound(Double upperBound) {
        if (upperBound == null) {
            this.upperBound = Option.empty();
        } else {
            this.upperBound = Option.of(upperBound);
        }
        return this;
    }

    /**
     * Set the anomalyDetectorDirection property: detection direction.
     *
     * @param anomalyDetectorDirection the anomalyDetectorDirection value to set.
     * @return the HardThresholdConditionPatch object itself.
     */
    public HardThresholdConditionPatch setAnomalyDetectorDirection(AnomalyDetectorDirection anomalyDetectorDirection) {
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
     * @return the HardThresholdConditionPatch object itself.
     */
    public HardThresholdConditionPatch setSuppressCondition(SuppressConditionPatch suppressCondition) {
        if (suppressCondition == null) {
            this.suppressCondition = Option.empty();
        } else {
            this.suppressCondition = Option.of(suppressCondition);
        }
        return this;
    }
}
