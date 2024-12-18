// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.alertsmanagement.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.CoreUtils;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.alertsmanagement.models.Severity;
import com.azure.resourcemanager.alertsmanagement.models.SmartGroupAggregatedProperty;
import com.azure.resourcemanager.alertsmanagement.models.State;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Properties of smart group.
 */
@Fluent
public final class SmartGroupProperties implements JsonSerializable<SmartGroupProperties> {
    /*
     * Total number of alerts in smart group
     */
    private Long alertsCount;

    /*
     * Smart group state
     */
    private State smartGroupState;

    /*
     * Severity of smart group is the highest(Sev0 >... > Sev4) severity of all the alerts in the group.
     */
    private Severity severity;

    /*
     * Creation time of smart group. Date-Time in ISO-8601 format.
     */
    private OffsetDateTime startDateTime;

    /*
     * Last updated time of smart group. Date-Time in ISO-8601 format.
     */
    private OffsetDateTime lastModifiedDateTime;

    /*
     * Last modified by user name.
     */
    private String lastModifiedUsername;

    /*
     * Summary of target resources in the smart group
     */
    private List<SmartGroupAggregatedProperty> resources;

    /*
     * Summary of target resource types in the smart group
     */
    private List<SmartGroupAggregatedProperty> resourceTypes;

    /*
     * Summary of target resource groups in the smart group
     */
    private List<SmartGroupAggregatedProperty> resourceGroups;

    /*
     * Summary of monitorServices in the smart group
     */
    private List<SmartGroupAggregatedProperty> monitorServices;

    /*
     * Summary of monitorConditions in the smart group
     */
    private List<SmartGroupAggregatedProperty> monitorConditions;

    /*
     * Summary of alertStates in the smart group
     */
    private List<SmartGroupAggregatedProperty> alertStates;

    /*
     * Summary of alertSeverities in the smart group
     */
    private List<SmartGroupAggregatedProperty> alertSeverities;

    /*
     * The URI to fetch the next page of alerts. Call ListNext() with this URI to fetch the next page alerts.
     */
    private String nextLink;

    /**
     * Creates an instance of SmartGroupProperties class.
     */
    public SmartGroupProperties() {
    }

    /**
     * Get the alertsCount property: Total number of alerts in smart group.
     * 
     * @return the alertsCount value.
     */
    public Long alertsCount() {
        return this.alertsCount;
    }

    /**
     * Set the alertsCount property: Total number of alerts in smart group.
     * 
     * @param alertsCount the alertsCount value to set.
     * @return the SmartGroupProperties object itself.
     */
    public SmartGroupProperties withAlertsCount(Long alertsCount) {
        this.alertsCount = alertsCount;
        return this;
    }

    /**
     * Get the smartGroupState property: Smart group state.
     * 
     * @return the smartGroupState value.
     */
    public State smartGroupState() {
        return this.smartGroupState;
    }

    /**
     * Get the severity property: Severity of smart group is the highest(Sev0 &gt;... &gt; Sev4) severity of all the
     * alerts in the group.
     * 
     * @return the severity value.
     */
    public Severity severity() {
        return this.severity;
    }

    /**
     * Get the startDateTime property: Creation time of smart group. Date-Time in ISO-8601 format.
     * 
     * @return the startDateTime value.
     */
    public OffsetDateTime startDateTime() {
        return this.startDateTime;
    }

    /**
     * Get the lastModifiedDateTime property: Last updated time of smart group. Date-Time in ISO-8601 format.
     * 
     * @return the lastModifiedDateTime value.
     */
    public OffsetDateTime lastModifiedDateTime() {
        return this.lastModifiedDateTime;
    }

    /**
     * Get the lastModifiedUsername property: Last modified by user name.
     * 
     * @return the lastModifiedUsername value.
     */
    public String lastModifiedUsername() {
        return this.lastModifiedUsername;
    }

    /**
     * Get the resources property: Summary of target resources in the smart group.
     * 
     * @return the resources value.
     */
    public List<SmartGroupAggregatedProperty> resources() {
        return this.resources;
    }

    /**
     * Set the resources property: Summary of target resources in the smart group.
     * 
     * @param resources the resources value to set.
     * @return the SmartGroupProperties object itself.
     */
    public SmartGroupProperties withResources(List<SmartGroupAggregatedProperty> resources) {
        this.resources = resources;
        return this;
    }

    /**
     * Get the resourceTypes property: Summary of target resource types in the smart group.
     * 
     * @return the resourceTypes value.
     */
    public List<SmartGroupAggregatedProperty> resourceTypes() {
        return this.resourceTypes;
    }

    /**
     * Set the resourceTypes property: Summary of target resource types in the smart group.
     * 
     * @param resourceTypes the resourceTypes value to set.
     * @return the SmartGroupProperties object itself.
     */
    public SmartGroupProperties withResourceTypes(List<SmartGroupAggregatedProperty> resourceTypes) {
        this.resourceTypes = resourceTypes;
        return this;
    }

    /**
     * Get the resourceGroups property: Summary of target resource groups in the smart group.
     * 
     * @return the resourceGroups value.
     */
    public List<SmartGroupAggregatedProperty> resourceGroups() {
        return this.resourceGroups;
    }

    /**
     * Set the resourceGroups property: Summary of target resource groups in the smart group.
     * 
     * @param resourceGroups the resourceGroups value to set.
     * @return the SmartGroupProperties object itself.
     */
    public SmartGroupProperties withResourceGroups(List<SmartGroupAggregatedProperty> resourceGroups) {
        this.resourceGroups = resourceGroups;
        return this;
    }

    /**
     * Get the monitorServices property: Summary of monitorServices in the smart group.
     * 
     * @return the monitorServices value.
     */
    public List<SmartGroupAggregatedProperty> monitorServices() {
        return this.monitorServices;
    }

    /**
     * Set the monitorServices property: Summary of monitorServices in the smart group.
     * 
     * @param monitorServices the monitorServices value to set.
     * @return the SmartGroupProperties object itself.
     */
    public SmartGroupProperties withMonitorServices(List<SmartGroupAggregatedProperty> monitorServices) {
        this.monitorServices = monitorServices;
        return this;
    }

    /**
     * Get the monitorConditions property: Summary of monitorConditions in the smart group.
     * 
     * @return the monitorConditions value.
     */
    public List<SmartGroupAggregatedProperty> monitorConditions() {
        return this.monitorConditions;
    }

    /**
     * Set the monitorConditions property: Summary of monitorConditions in the smart group.
     * 
     * @param monitorConditions the monitorConditions value to set.
     * @return the SmartGroupProperties object itself.
     */
    public SmartGroupProperties withMonitorConditions(List<SmartGroupAggregatedProperty> monitorConditions) {
        this.monitorConditions = monitorConditions;
        return this;
    }

    /**
     * Get the alertStates property: Summary of alertStates in the smart group.
     * 
     * @return the alertStates value.
     */
    public List<SmartGroupAggregatedProperty> alertStates() {
        return this.alertStates;
    }

    /**
     * Set the alertStates property: Summary of alertStates in the smart group.
     * 
     * @param alertStates the alertStates value to set.
     * @return the SmartGroupProperties object itself.
     */
    public SmartGroupProperties withAlertStates(List<SmartGroupAggregatedProperty> alertStates) {
        this.alertStates = alertStates;
        return this;
    }

    /**
     * Get the alertSeverities property: Summary of alertSeverities in the smart group.
     * 
     * @return the alertSeverities value.
     */
    public List<SmartGroupAggregatedProperty> alertSeverities() {
        return this.alertSeverities;
    }

    /**
     * Set the alertSeverities property: Summary of alertSeverities in the smart group.
     * 
     * @param alertSeverities the alertSeverities value to set.
     * @return the SmartGroupProperties object itself.
     */
    public SmartGroupProperties withAlertSeverities(List<SmartGroupAggregatedProperty> alertSeverities) {
        this.alertSeverities = alertSeverities;
        return this;
    }

    /**
     * Get the nextLink property: The URI to fetch the next page of alerts. Call ListNext() with this URI to fetch the
     * next page alerts.
     * 
     * @return the nextLink value.
     */
    public String nextLink() {
        return this.nextLink;
    }

    /**
     * Set the nextLink property: The URI to fetch the next page of alerts. Call ListNext() with this URI to fetch the
     * next page alerts.
     * 
     * @param nextLink the nextLink value to set.
     * @return the SmartGroupProperties object itself.
     */
    public SmartGroupProperties withNextLink(String nextLink) {
        this.nextLink = nextLink;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (resources() != null) {
            resources().forEach(e -> e.validate());
        }
        if (resourceTypes() != null) {
            resourceTypes().forEach(e -> e.validate());
        }
        if (resourceGroups() != null) {
            resourceGroups().forEach(e -> e.validate());
        }
        if (monitorServices() != null) {
            monitorServices().forEach(e -> e.validate());
        }
        if (monitorConditions() != null) {
            monitorConditions().forEach(e -> e.validate());
        }
        if (alertStates() != null) {
            alertStates().forEach(e -> e.validate());
        }
        if (alertSeverities() != null) {
            alertSeverities().forEach(e -> e.validate());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeNumberField("alertsCount", this.alertsCount);
        jsonWriter.writeArrayField("resources", this.resources, (writer, element) -> writer.writeJson(element));
        jsonWriter.writeArrayField("resourceTypes", this.resourceTypes, (writer, element) -> writer.writeJson(element));
        jsonWriter.writeArrayField("resourceGroups", this.resourceGroups,
            (writer, element) -> writer.writeJson(element));
        jsonWriter.writeArrayField("monitorServices", this.monitorServices,
            (writer, element) -> writer.writeJson(element));
        jsonWriter.writeArrayField("monitorConditions", this.monitorConditions,
            (writer, element) -> writer.writeJson(element));
        jsonWriter.writeArrayField("alertStates", this.alertStates, (writer, element) -> writer.writeJson(element));
        jsonWriter.writeArrayField("alertSeverities", this.alertSeverities,
            (writer, element) -> writer.writeJson(element));
        jsonWriter.writeStringField("nextLink", this.nextLink);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of SmartGroupProperties from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of SmartGroupProperties if the JsonReader was pointing to an instance of it, or null if it
     * was pointing to JSON null.
     * @throws IOException If an error occurs while reading the SmartGroupProperties.
     */
    public static SmartGroupProperties fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            SmartGroupProperties deserializedSmartGroupProperties = new SmartGroupProperties();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("alertsCount".equals(fieldName)) {
                    deserializedSmartGroupProperties.alertsCount = reader.getNullable(JsonReader::getLong);
                } else if ("smartGroupState".equals(fieldName)) {
                    deserializedSmartGroupProperties.smartGroupState = State.fromString(reader.getString());
                } else if ("severity".equals(fieldName)) {
                    deserializedSmartGroupProperties.severity = Severity.fromString(reader.getString());
                } else if ("startDateTime".equals(fieldName)) {
                    deserializedSmartGroupProperties.startDateTime = reader
                        .getNullable(nonNullReader -> CoreUtils.parseBestOffsetDateTime(nonNullReader.getString()));
                } else if ("lastModifiedDateTime".equals(fieldName)) {
                    deserializedSmartGroupProperties.lastModifiedDateTime = reader
                        .getNullable(nonNullReader -> CoreUtils.parseBestOffsetDateTime(nonNullReader.getString()));
                } else if ("lastModifiedUserName".equals(fieldName)) {
                    deserializedSmartGroupProperties.lastModifiedUsername = reader.getString();
                } else if ("resources".equals(fieldName)) {
                    List<SmartGroupAggregatedProperty> resources
                        = reader.readArray(reader1 -> SmartGroupAggregatedProperty.fromJson(reader1));
                    deserializedSmartGroupProperties.resources = resources;
                } else if ("resourceTypes".equals(fieldName)) {
                    List<SmartGroupAggregatedProperty> resourceTypes
                        = reader.readArray(reader1 -> SmartGroupAggregatedProperty.fromJson(reader1));
                    deserializedSmartGroupProperties.resourceTypes = resourceTypes;
                } else if ("resourceGroups".equals(fieldName)) {
                    List<SmartGroupAggregatedProperty> resourceGroups
                        = reader.readArray(reader1 -> SmartGroupAggregatedProperty.fromJson(reader1));
                    deserializedSmartGroupProperties.resourceGroups = resourceGroups;
                } else if ("monitorServices".equals(fieldName)) {
                    List<SmartGroupAggregatedProperty> monitorServices
                        = reader.readArray(reader1 -> SmartGroupAggregatedProperty.fromJson(reader1));
                    deserializedSmartGroupProperties.monitorServices = monitorServices;
                } else if ("monitorConditions".equals(fieldName)) {
                    List<SmartGroupAggregatedProperty> monitorConditions
                        = reader.readArray(reader1 -> SmartGroupAggregatedProperty.fromJson(reader1));
                    deserializedSmartGroupProperties.monitorConditions = monitorConditions;
                } else if ("alertStates".equals(fieldName)) {
                    List<SmartGroupAggregatedProperty> alertStates
                        = reader.readArray(reader1 -> SmartGroupAggregatedProperty.fromJson(reader1));
                    deserializedSmartGroupProperties.alertStates = alertStates;
                } else if ("alertSeverities".equals(fieldName)) {
                    List<SmartGroupAggregatedProperty> alertSeverities
                        = reader.readArray(reader1 -> SmartGroupAggregatedProperty.fromJson(reader1));
                    deserializedSmartGroupProperties.alertSeverities = alertSeverities;
                } else if ("nextLink".equals(fieldName)) {
                    deserializedSmartGroupProperties.nextLink = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedSmartGroupProperties;
        });
    }
}
