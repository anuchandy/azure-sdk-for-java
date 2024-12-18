// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.securityinsights.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.util.List;

/**
 * alert rule template data sources.
 */
@Fluent
public final class AlertRuleTemplateDataSource implements JsonSerializable<AlertRuleTemplateDataSource> {
    /*
     * The connector id that provides the following data types
     */
    private String connectorId;

    /*
     * The data types used by the alert rule template
     */
    private List<String> dataTypes;

    /**
     * Creates an instance of AlertRuleTemplateDataSource class.
     */
    public AlertRuleTemplateDataSource() {
    }

    /**
     * Get the connectorId property: The connector id that provides the following data types.
     * 
     * @return the connectorId value.
     */
    public String connectorId() {
        return this.connectorId;
    }

    /**
     * Set the connectorId property: The connector id that provides the following data types.
     * 
     * @param connectorId the connectorId value to set.
     * @return the AlertRuleTemplateDataSource object itself.
     */
    public AlertRuleTemplateDataSource withConnectorId(String connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    /**
     * Get the dataTypes property: The data types used by the alert rule template.
     * 
     * @return the dataTypes value.
     */
    public List<String> dataTypes() {
        return this.dataTypes;
    }

    /**
     * Set the dataTypes property: The data types used by the alert rule template.
     * 
     * @param dataTypes the dataTypes value to set.
     * @return the AlertRuleTemplateDataSource object itself.
     */
    public AlertRuleTemplateDataSource withDataTypes(List<String> dataTypes) {
        this.dataTypes = dataTypes;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("connectorId", this.connectorId);
        jsonWriter.writeArrayField("dataTypes", this.dataTypes, (writer, element) -> writer.writeString(element));
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of AlertRuleTemplateDataSource from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of AlertRuleTemplateDataSource if the JsonReader was pointing to an instance of it, or null
     * if it was pointing to JSON null.
     * @throws IOException If an error occurs while reading the AlertRuleTemplateDataSource.
     */
    public static AlertRuleTemplateDataSource fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            AlertRuleTemplateDataSource deserializedAlertRuleTemplateDataSource = new AlertRuleTemplateDataSource();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("connectorId".equals(fieldName)) {
                    deserializedAlertRuleTemplateDataSource.connectorId = reader.getString();
                } else if ("dataTypes".equals(fieldName)) {
                    List<String> dataTypes = reader.readArray(reader1 -> reader1.getString());
                    deserializedAlertRuleTemplateDataSource.dataTypes = dataTypes;
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedAlertRuleTemplateDataSource;
        });
    }
}
