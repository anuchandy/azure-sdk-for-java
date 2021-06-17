// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.ai.metricsadvisor.models.MetricAlertingConfiguration;
import com.azure.ai.metricsadvisor.models.MetricAnomalyAlertConfigurationsOperator;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** The AnomalyAlertingConfigurationPatch model. */
@Fluent
public final class AnomalyAlertingConfigurationPatch {
    /*
     * Anomaly alerting configuration name
     */
    @JsonProperty(value = "name")
    private Option<String> name;

    /*
     * anomaly alerting configuration description
     */
    @JsonProperty(value = "description")
    private Option<String> description;

    /*
     * cross metrics operator
     */
    @JsonProperty(value = "crossMetricsOperator")
    private Option<MetricAnomalyAlertConfigurationsOperator> crossMetricsOperator;

    /*
     * dimensions used to split alert
     */
    @JsonProperty(value = "splitAlertByDimensions")
    private Option<List<String>> splitAlertByDimensions;

    /*
     * hook unique ids
     */
    @JsonProperty(value = "hookIds")
    private Option<List<UUID>> hookIds;

    /*
     * Anomaly alerting configurations
     */
    @JsonProperty(value = "metricAlertingConfigurations")
    private Option<List<MetricAlertingConfiguration>> metricAlertingConfigurations;

    /**
     * Set the name property: Anomaly alerting configuration name.
     *
     * @param name the name value to set.
     * @return the AnomalyAlertingConfigurationPatch object itself.
     */
    public AnomalyAlertingConfigurationPatch setName(String name) {
        if (name == null) {
            this.name = Option.empty();
        } else {
            this.name = Option.of(name);
        }
        return this;
    }

    /**
     * Set the description property: anomaly alerting configuration description.
     *
     * @param description the description value to set.
     * @return the AnomalyAlertingConfigurationPatch object itself.
     */
    public AnomalyAlertingConfigurationPatch setDescription(String description) {
        if (description == null) {
            this.description = Option.empty();
        } else {
            this.description = Option.of(description);
        }
        return this;
    }

    /**
     * Set the crossMetricsOperator property: cross metrics operator.
     *
     * @param crossMetricsOperator the crossMetricsOperator value to set.
     * @return the AnomalyAlertingConfigurationPatch object itself.
     */
    public AnomalyAlertingConfigurationPatch setCrossMetricsOperator(
        MetricAnomalyAlertConfigurationsOperator crossMetricsOperator) {
        if (crossMetricsOperator == null) {
            this.crossMetricsOperator = Option.empty();
        } else {
            this.crossMetricsOperator = Option.of(crossMetricsOperator);
        }
        return this;
    }

    /**
     * Set the splitAlertByDimensions property: dimensions used to split alert.
     *
     * @param splitAlertByDimensions the splitAlertByDimensions value to set.
     * @return the AnomalyAlertingConfigurationPatch object itself.
     */
    public AnomalyAlertingConfigurationPatch setSplitAlertByDimensions(List<String> splitAlertByDimensions) {
        if (splitAlertByDimensions == null) {
            this.splitAlertByDimensions = Option.empty();
        } else {
            this.splitAlertByDimensions = Option.of(splitAlertByDimensions);
        }
        return this;
    }

    /**
     * Set the hookIds property: hook unique ids.
     *
     * @param hookIds the hookIds value to set.
     * @return the AnomalyAlertingConfigurationPatch object itself.
     */
    public AnomalyAlertingConfigurationPatch setHookIds(List<String> hookIds) {
        if (hookIds == null) {
            this.hookIds = Option.empty();
        } else {
            List<UUID> uuids = new ArrayList<>();
            for (String hookId : hookIds) {
                uuids.add(UUID.fromString(hookId));
            }
            this.hookIds = Option.of(uuids);
        }
        return this;
    }

    /**
     * Set the metricAlertingConfigurations property: Anomaly alerting configurations.
     *
     * @param metricAlertingConfigurations the metricAlertingConfigurations value to set.
     * @return the AnomalyAlertingConfigurationPatch object itself.
     */
    public AnomalyAlertingConfigurationPatch setMetricAlertingConfigurations(
            List<MetricAlertingConfiguration> metricAlertingConfigurations) {
        if (metricAlertingConfigurations == null) {
            this.metricAlertingConfigurations = Option.empty();
        } else {
            this.metricAlertingConfigurations = Option.of(metricAlertingConfigurations);
        }
        return this;
    }
}
