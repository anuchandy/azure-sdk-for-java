// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.models.DimensionGroupConfiguration;
import com.azure.ai.metricsadvisor.implementation.models.SeriesConfiguration;
import com.azure.ai.metricsadvisor.implementation.util.DetectionConfigurationTransforms;
import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.ai.metricsadvisor.models.MetricSeriesGroupDetectionCondition;
import com.azure.ai.metricsadvisor.models.MetricSingleSeriesDetectionCondition;
import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/** The AnomalyDetectionConfigurationPatch model. */
@Fluent
public final class AnomalyDetectionConfigurationPatch {
    /*
     * anomaly detection configuration name
     */
    @JsonProperty(value = "name")
    private Option<String> name;

    /*
     * anomaly detection configuration description
     */
    @JsonProperty(value = "description")
    private Option<String> description;

    /*
     * The wholeMetricConfiguration property.
     */
    @JsonProperty(value = "wholeMetricConfiguration")
    private Option<WholeMetricConfigurationPatch> wholeMetricConfiguration;

    /*
     * detection configuration for series group
     */
    @JsonProperty(value = "dimensionGroupOverrideConfigurations")
    private Option<List<DimensionGroupConfiguration>> dimensionGroupOverrideConfigurations;

    /*
     * detection configuration for specific series
     */
    @JsonProperty(value = "seriesOverrideConfigurations")
    private Option<List<SeriesConfiguration>> seriesOverrideConfigurations;

    /**
     * Set the name property: anomaly detection configuration name.
     *
     * @param name the name value to set.
     * @return the AnomalyDetectionConfigurationPatch object itself.
     */
    public AnomalyDetectionConfigurationPatch setName(String name) {
        if (name == null) {
            this.name = Option.empty();
        } else {
            this.name = Option.of(name);
        }
        return this;
    }

    /**
     * Set the description property: anomaly detection configuration description.
     *
     * @param description the description value to set.
     * @return the AnomalyDetectionConfigurationPatch object itself.
     */
    public AnomalyDetectionConfigurationPatch setDescription(String description) {
        if (description == null) {
            this.description = Option.empty();
        } else {
            this.description = Option.of(description);
        }
        return this;
    }

    /**
     * Set the wholeMetricConfiguration property: The wholeMetricConfiguration property.
     *
     * @param wholeMetricConfiguration the wholeMetricConfiguration value to set.
     * @return the AnomalyDetectionConfigurationPatch object itself.
     */
    public AnomalyDetectionConfigurationPatch setWholeMetricConfiguration(
            WholeMetricConfigurationPatch wholeMetricConfiguration) {
        if (wholeMetricConfiguration == null) {
            this.wholeMetricConfiguration = Option.empty();
        } else {
            this.wholeMetricConfiguration = Option.of(wholeMetricConfiguration);
        }
        return this;
    }

    /**
     * Set the dimensionGroupOverrideConfigurations property: detection configuration for series group.
     *
     * @param seriesGroupConfigurations the dimensionGroupOverrideConfigurations value to set.
     * @return the AnomalyDetectionConfigurationPatch object itself.
     */
    public AnomalyDetectionConfigurationPatch setSeriesGroupConfigurations(
            List<MetricSeriesGroupDetectionCondition> seriesGroupConfigurations) {
        if (seriesGroupConfigurations == null) {
            this.dimensionGroupOverrideConfigurations = Option.empty();
        } else {
            List<DimensionGroupConfiguration> innerDimensionGroupOverrideConfigurations = new ArrayList<>();
            for (MetricSeriesGroupDetectionCondition config : seriesGroupConfigurations) {
                DimensionGroupConfiguration innerConfig = DetectionConfigurationTransforms
                    .setupInnerSeriesGroupConfiguration(new ClientLogger(AnomalyDetectionConfigurationPatch.class),
                        false,
                        config);
                innerDimensionGroupOverrideConfigurations.add(innerConfig);
            }
            this.dimensionGroupOverrideConfigurations = Option.of(innerDimensionGroupOverrideConfigurations);
        }
        return this;
    }

    /**
     * Set the seriesOverrideConfigurations property: detection configuration for specific series.
     *
     * @param singleSeriesConfigurations the seriesOverrideConfigurations value to set.
     * @return the AnomalyDetectionConfigurationPatch object itself.
     */
    public AnomalyDetectionConfigurationPatch setSingleSeriesConfigurations(
            List<MetricSingleSeriesDetectionCondition> singleSeriesConfigurations) {
        if (singleSeriesConfigurations == null) {
            this.seriesOverrideConfigurations = Option.empty();
        } else {
            List<SeriesConfiguration> innerSeriesOverrideConfigurations = new ArrayList<>();
            for (MetricSingleSeriesDetectionCondition config : singleSeriesConfigurations) {
                SeriesConfiguration innerConfig = DetectionConfigurationTransforms.setupInnerSeriesConfiguration(
                    new ClientLogger(AnomalyDetectionConfigurationPatch.class), false, config);
                innerSeriesOverrideConfigurations.add(innerConfig);
            }
            this.seriesOverrideConfigurations = Option.of(innerSeriesOverrideConfigurations);
        }
        return this;
    }
}
