// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.ai.metricsadvisor.models.AnomalyDetectorDirection;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The ChangeThresholdConditionPatch model. */
@Fluent
public final class ChangeThresholdConditionPatch {
    /*
     * change percentage, value range : [0, +∞)
     */
    @JsonProperty(value = "changePercentage")
    private Option<Double> changePercentage;

    /*
     * shift point, value range : [1, +∞)
     */
    @JsonProperty(value = "shiftPoint")
    private Option<Integer> shiftPoint;

    /*
     * if the withinRange = true, detected data is abnormal when the value
     * falls in the range, in this case anomalyDetectorDirection must be Both
     * if the withinRange = false, detected data is abnormal when the value
     * falls out of the range
     */
    @JsonProperty(value = "withinRange")
    private Option<Boolean> withinRange;

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
     * Set the changePercentage property: change percentage, value range : [0, +∞).
     *
     * @param changePercentage the changePercentage value to set.
     * @return the ChangeThresholdConditionPatch object itself.
     */
    public ChangeThresholdConditionPatch setChangePercentage(Double changePercentage) {
        if (changePercentage == null) {
            this.changePercentage = Option.empty();
        } else {
            this.changePercentage = Option.of(changePercentage);
        }
        return this;
    }

    /**
     * Set the shiftPoint property: shift point, value range : [1, +∞).
     *
     * @param shiftPoint the shiftPoint value to set.
     * @return the ChangeThresholdConditionPatch object itself.
     */
    public ChangeThresholdConditionPatch setShiftPoint(Integer shiftPoint) {
        if (shiftPoint == null) {
            this.shiftPoint = Option.empty();
        } else {
            this.shiftPoint = Option.of(shiftPoint);
        }
        return this;
    }

    /**
     * Set the withinRange property: if the withinRange = true, detected data is abnormal when the value falls in the
     * range, in this case anomalyDetectorDirection must be Both if the withinRange = false, detected data is abnormal
     * when the value falls out of the range.
     *
     * @param withinRange the withinRange value to set.
     * @return the ChangeThresholdConditionPatch object itself.
     */
    public ChangeThresholdConditionPatch setWithinRange(Boolean withinRange) {
        if (withinRange == null) {
            this.withinRange = Option.empty();
        } else {
            this.withinRange = Option.of(withinRange);
        }
        return this;
    }

    /**
     * Set the anomalyDetectorDirection property: detection direction.
     *
     * @param anomalyDetectorDirection the anomalyDetectorDirection value to set.
     * @return the ChangeThresholdConditionPatch object itself.
     */
    public ChangeThresholdConditionPatch setAnomalyDetectorDirection(
            AnomalyDetectorDirection anomalyDetectorDirection) {
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
     * @return the ChangeThresholdConditionPatch object itself.
     */
    public ChangeThresholdConditionPatch setSuppressCondition(SuppressConditionPatch suppressCondition) {
        if (suppressCondition == null) {
            this.suppressCondition = Option.empty();
        } else {
            this.suppressCondition = Option.of(suppressCondition);
        }
        return this;
    }
}
