// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.customerinsights.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.customerinsights.models.EntityType;
import com.azure.resourcemanager.customerinsights.models.InstanceOperationType;
import com.azure.resourcemanager.customerinsights.models.ParticipantPropertyReference;
import com.azure.resourcemanager.customerinsights.models.ProvisioningStates;
import com.azure.resourcemanager.customerinsights.models.TypePropertiesMapping;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The definition of Link.
 */
@Fluent
public final class LinkDefinition implements JsonSerializable<LinkDefinition> {
    /*
     * The hub name.
     */
    private String tenantId;

    /*
     * The link name.
     */
    private String linkName;

    /*
     * Type of source entity.
     */
    private EntityType sourceEntityType;

    /*
     * Type of target entity.
     */
    private EntityType targetEntityType;

    /*
     * Name of the source Entity Type.
     */
    private String sourceEntityTypeName;

    /*
     * Name of the target Entity Type.
     */
    private String targetEntityTypeName;

    /*
     * Localized display name for the Link.
     */
    private Map<String, String> displayName;

    /*
     * Localized descriptions for the Link.
     */
    private Map<String, String> description;

    /*
     * The set of properties mappings between the source and target Types.
     */
    private List<TypePropertiesMapping> mappings;

    /*
     * The properties that represent the participating profile.
     */
    private List<ParticipantPropertyReference> participantPropertyReferences;

    /*
     * Provisioning state.
     */
    private ProvisioningStates provisioningState;

    /*
     * Indicating whether the link is reference only link. This flag is ignored if the Mappings are defined. If the
     * mappings are not defined and it is set to true, links processing will not create or update profiles.
     */
    private Boolean referenceOnly;

    /*
     * Determines whether this link is supposed to create or delete instances if Link is NOT Reference Only.
     */
    private InstanceOperationType operationType;

    /**
     * Creates an instance of LinkDefinition class.
     */
    public LinkDefinition() {
    }

    /**
     * Get the tenantId property: The hub name.
     * 
     * @return the tenantId value.
     */
    public String tenantId() {
        return this.tenantId;
    }

    /**
     * Get the linkName property: The link name.
     * 
     * @return the linkName value.
     */
    public String linkName() {
        return this.linkName;
    }

    /**
     * Get the sourceEntityType property: Type of source entity.
     * 
     * @return the sourceEntityType value.
     */
    public EntityType sourceEntityType() {
        return this.sourceEntityType;
    }

    /**
     * Set the sourceEntityType property: Type of source entity.
     * 
     * @param sourceEntityType the sourceEntityType value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition withSourceEntityType(EntityType sourceEntityType) {
        this.sourceEntityType = sourceEntityType;
        return this;
    }

    /**
     * Get the targetEntityType property: Type of target entity.
     * 
     * @return the targetEntityType value.
     */
    public EntityType targetEntityType() {
        return this.targetEntityType;
    }

    /**
     * Set the targetEntityType property: Type of target entity.
     * 
     * @param targetEntityType the targetEntityType value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition withTargetEntityType(EntityType targetEntityType) {
        this.targetEntityType = targetEntityType;
        return this;
    }

    /**
     * Get the sourceEntityTypeName property: Name of the source Entity Type.
     * 
     * @return the sourceEntityTypeName value.
     */
    public String sourceEntityTypeName() {
        return this.sourceEntityTypeName;
    }

    /**
     * Set the sourceEntityTypeName property: Name of the source Entity Type.
     * 
     * @param sourceEntityTypeName the sourceEntityTypeName value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition withSourceEntityTypeName(String sourceEntityTypeName) {
        this.sourceEntityTypeName = sourceEntityTypeName;
        return this;
    }

    /**
     * Get the targetEntityTypeName property: Name of the target Entity Type.
     * 
     * @return the targetEntityTypeName value.
     */
    public String targetEntityTypeName() {
        return this.targetEntityTypeName;
    }

    /**
     * Set the targetEntityTypeName property: Name of the target Entity Type.
     * 
     * @param targetEntityTypeName the targetEntityTypeName value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition withTargetEntityTypeName(String targetEntityTypeName) {
        this.targetEntityTypeName = targetEntityTypeName;
        return this;
    }

    /**
     * Get the displayName property: Localized display name for the Link.
     * 
     * @return the displayName value.
     */
    public Map<String, String> displayName() {
        return this.displayName;
    }

    /**
     * Set the displayName property: Localized display name for the Link.
     * 
     * @param displayName the displayName value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition withDisplayName(Map<String, String> displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Get the description property: Localized descriptions for the Link.
     * 
     * @return the description value.
     */
    public Map<String, String> description() {
        return this.description;
    }

    /**
     * Set the description property: Localized descriptions for the Link.
     * 
     * @param description the description value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition withDescription(Map<String, String> description) {
        this.description = description;
        return this;
    }

    /**
     * Get the mappings property: The set of properties mappings between the source and target Types.
     * 
     * @return the mappings value.
     */
    public List<TypePropertiesMapping> mappings() {
        return this.mappings;
    }

    /**
     * Set the mappings property: The set of properties mappings between the source and target Types.
     * 
     * @param mappings the mappings value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition withMappings(List<TypePropertiesMapping> mappings) {
        this.mappings = mappings;
        return this;
    }

    /**
     * Get the participantPropertyReferences property: The properties that represent the participating profile.
     * 
     * @return the participantPropertyReferences value.
     */
    public List<ParticipantPropertyReference> participantPropertyReferences() {
        return this.participantPropertyReferences;
    }

    /**
     * Set the participantPropertyReferences property: The properties that represent the participating profile.
     * 
     * @param participantPropertyReferences the participantPropertyReferences value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition
        withParticipantPropertyReferences(List<ParticipantPropertyReference> participantPropertyReferences) {
        this.participantPropertyReferences = participantPropertyReferences;
        return this;
    }

    /**
     * Get the provisioningState property: Provisioning state.
     * 
     * @return the provisioningState value.
     */
    public ProvisioningStates provisioningState() {
        return this.provisioningState;
    }

    /**
     * Get the referenceOnly property: Indicating whether the link is reference only link. This flag is ignored if the
     * Mappings are defined. If the mappings are not defined and it is set to true, links processing will not create or
     * update profiles.
     * 
     * @return the referenceOnly value.
     */
    public Boolean referenceOnly() {
        return this.referenceOnly;
    }

    /**
     * Set the referenceOnly property: Indicating whether the link is reference only link. This flag is ignored if the
     * Mappings are defined. If the mappings are not defined and it is set to true, links processing will not create or
     * update profiles.
     * 
     * @param referenceOnly the referenceOnly value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition withReferenceOnly(Boolean referenceOnly) {
        this.referenceOnly = referenceOnly;
        return this;
    }

    /**
     * Get the operationType property: Determines whether this link is supposed to create or delete instances if Link is
     * NOT Reference Only.
     * 
     * @return the operationType value.
     */
    public InstanceOperationType operationType() {
        return this.operationType;
    }

    /**
     * Set the operationType property: Determines whether this link is supposed to create or delete instances if Link is
     * NOT Reference Only.
     * 
     * @param operationType the operationType value to set.
     * @return the LinkDefinition object itself.
     */
    public LinkDefinition withOperationType(InstanceOperationType operationType) {
        this.operationType = operationType;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (sourceEntityType() == null) {
            throw LOGGER.atError()
                .log(
                    new IllegalArgumentException("Missing required property sourceEntityType in model LinkDefinition"));
        }
        if (targetEntityType() == null) {
            throw LOGGER.atError()
                .log(
                    new IllegalArgumentException("Missing required property targetEntityType in model LinkDefinition"));
        }
        if (sourceEntityTypeName() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException(
                    "Missing required property sourceEntityTypeName in model LinkDefinition"));
        }
        if (targetEntityTypeName() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException(
                    "Missing required property targetEntityTypeName in model LinkDefinition"));
        }
        if (mappings() != null) {
            mappings().forEach(e -> e.validate());
        }
        if (participantPropertyReferences() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException(
                    "Missing required property participantPropertyReferences in model LinkDefinition"));
        } else {
            participantPropertyReferences().forEach(e -> e.validate());
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(LinkDefinition.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("sourceEntityType",
            this.sourceEntityType == null ? null : this.sourceEntityType.toString());
        jsonWriter.writeStringField("targetEntityType",
            this.targetEntityType == null ? null : this.targetEntityType.toString());
        jsonWriter.writeStringField("sourceEntityTypeName", this.sourceEntityTypeName);
        jsonWriter.writeStringField("targetEntityTypeName", this.targetEntityTypeName);
        jsonWriter.writeArrayField("participantPropertyReferences", this.participantPropertyReferences,
            (writer, element) -> writer.writeJson(element));
        jsonWriter.writeMapField("displayName", this.displayName, (writer, element) -> writer.writeString(element));
        jsonWriter.writeMapField("description", this.description, (writer, element) -> writer.writeString(element));
        jsonWriter.writeArrayField("mappings", this.mappings, (writer, element) -> writer.writeJson(element));
        jsonWriter.writeBooleanField("referenceOnly", this.referenceOnly);
        jsonWriter.writeStringField("operationType", this.operationType == null ? null : this.operationType.toString());
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of LinkDefinition from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of LinkDefinition if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the LinkDefinition.
     */
    public static LinkDefinition fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            LinkDefinition deserializedLinkDefinition = new LinkDefinition();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("sourceEntityType".equals(fieldName)) {
                    deserializedLinkDefinition.sourceEntityType = EntityType.fromString(reader.getString());
                } else if ("targetEntityType".equals(fieldName)) {
                    deserializedLinkDefinition.targetEntityType = EntityType.fromString(reader.getString());
                } else if ("sourceEntityTypeName".equals(fieldName)) {
                    deserializedLinkDefinition.sourceEntityTypeName = reader.getString();
                } else if ("targetEntityTypeName".equals(fieldName)) {
                    deserializedLinkDefinition.targetEntityTypeName = reader.getString();
                } else if ("participantPropertyReferences".equals(fieldName)) {
                    List<ParticipantPropertyReference> participantPropertyReferences
                        = reader.readArray(reader1 -> ParticipantPropertyReference.fromJson(reader1));
                    deserializedLinkDefinition.participantPropertyReferences = participantPropertyReferences;
                } else if ("tenantId".equals(fieldName)) {
                    deserializedLinkDefinition.tenantId = reader.getString();
                } else if ("linkName".equals(fieldName)) {
                    deserializedLinkDefinition.linkName = reader.getString();
                } else if ("displayName".equals(fieldName)) {
                    Map<String, String> displayName = reader.readMap(reader1 -> reader1.getString());
                    deserializedLinkDefinition.displayName = displayName;
                } else if ("description".equals(fieldName)) {
                    Map<String, String> description = reader.readMap(reader1 -> reader1.getString());
                    deserializedLinkDefinition.description = description;
                } else if ("mappings".equals(fieldName)) {
                    List<TypePropertiesMapping> mappings
                        = reader.readArray(reader1 -> TypePropertiesMapping.fromJson(reader1));
                    deserializedLinkDefinition.mappings = mappings;
                } else if ("provisioningState".equals(fieldName)) {
                    deserializedLinkDefinition.provisioningState = ProvisioningStates.fromString(reader.getString());
                } else if ("referenceOnly".equals(fieldName)) {
                    deserializedLinkDefinition.referenceOnly = reader.getNullable(JsonReader::getBoolean);
                } else if ("operationType".equals(fieldName)) {
                    deserializedLinkDefinition.operationType = InstanceOperationType.fromString(reader.getString());
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedLinkDefinition;
        });
    }
}
