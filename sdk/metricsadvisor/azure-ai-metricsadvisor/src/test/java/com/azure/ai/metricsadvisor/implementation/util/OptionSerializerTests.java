// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.implementation.util;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.serializer.JacksonAdapter;
import com.azure.core.util.serializer.SerializerEncoding;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Tests for {@link Option} that can represent tri-sate (non-null-value, null-value, or no-value).
 */
public class OptionSerializerTests {
    private JacksonAdapter getSerializer() {
        JacksonAdapter jacksonAdapter = new JacksonAdapter();
        jacksonAdapter.serializer().registerModule(new OptionModule());
        return jacksonAdapter;
    }

    @Test
    public void canSerializeExplicitNull() throws IOException {
        PatchModel model = new PatchModel();

        model.setSku(Option.of(null));
        String serialized = getSerializer()
            .serialize(model, SerializerEncoding.JSON);
        Assertions.assertEquals("{\"sku\":null}", serialized);

        model.setSku(Option.empty());
        serialized = getSerializer().serialize(model, SerializerEncoding.JSON);
        Assertions.assertEquals("{\"sku\":null}", serialized);
    }

    @Test
    public void shouldIgnoreImplicitNull() throws IOException {
        PatchModel model = new PatchModel();
        String serialized = getSerializer().serialize(model, SerializerEncoding.JSON);
        Assertions.assertEquals("{}", serialized);
    }

    @Test
    public void shouldIgnoreUninitialized() throws IOException {
        PatchModel model = new PatchModel();
        model.setSku(Option.uninitialized());
        String serialized = getSerializer().serialize(model, SerializerEncoding.JSON);
        Assertions.assertEquals("{}", serialized);
    }

    @Test
    public void canSerializeNonNullValue() throws IOException {
        PatchModel model = new PatchModel();
        model.setSku(Option.of("basic"));
        String serialized = getSerializer().serialize(model, SerializerEncoding.JSON);
        Assertions.assertEquals("{\"sku\":\"basic\"}", serialized);
    }

    @Test
    public void canSerializeRawType() throws IOException {
        @SuppressWarnings("rawtypes")
        final Option rawOption = Option.of(new RawModel().setName("test"));
        String serialized = getSerializer().serialize(rawOption, SerializerEncoding.JSON);
        Assertions.assertEquals("{\"name\":\"test\"}", serialized);

        @SuppressWarnings("rawtypes")
        final Option rawOption1 = Option.of("test");
        String serialized1 = getSerializer().serialize(rawOption1, SerializerEncoding.JSON);
        Assertions.assertEquals("\"test\"", serialized1);
    }

    @Test
    public void canSerializePolymorphicType() throws IOException {
        ExtendedPatchModel patch = new ExtendedPatchModel();
        patch.setParameters(new ParamExtendedPatch().setConnectionString("con-str"));

        PolymorphicPatchModel basePatch = patch;
        final String serialized1 = getSerializer().serialize(basePatch, SerializerEncoding.JSON);
        Assertions
            .assertEquals(
                "{\"actualType\":\"PolymorphicPatch\",\"parameters\":{\"connectionString\":\"con-str\"}}",
                serialized1);

        patch = new ExtendedPatchModel();
        patch.setParameters(new ParamExtendedPatch().setConnectionString(null));

        basePatch = patch;
        final String serialized2 = getSerializer().serialize(basePatch, SerializerEncoding.JSON);
        Assertions
            .assertEquals(
                "{\"actualType\":\"PolymorphicPatch\",\"parameters\":{\"connectionString\":null}}",
                serialized2);

        patch = new ExtendedPatchModel();
        patch.setParameters(new ParamExtendedPatch());

        basePatch = patch;
        final String serialized3 = getSerializer().serialize(basePatch, SerializerEncoding.JSON);
        Assertions
            .assertEquals(
                "{\"actualType\":\"PolymorphicPatch\",\"parameters\":{}}",
                serialized3);
    }

    private static class PatchModel {
        @JsonProperty("sku")
        private Option<String> sku;

        PatchModel setSku(Option<String> sku) {
            this.sku = sku;
            return this;
        }
    }

    private static class RawModel {
        @JsonProperty("name")
        private String name;

        RawModel setName(String name) {
            this.name = name;
            return this;
        }
    }

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "actualType",
        defaultImpl = PolymorphicPatchModel.class)
    @JsonTypeName("PolymorphicPatch")
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "ExtendedPatch", value = ExtendedPatchModel.class),
    })
    @Fluent
    private static class PolymorphicPatchModel {

    }

    private static class ExtendedPatchModel extends PolymorphicPatchModel {
        @JsonProperty(value = "parameters")
        private ParamExtendedPatch parameters;

        public ExtendedPatchModel setParameters(ParamExtendedPatch parameters) {
            this.parameters = parameters;
            return this;
        }
    }

    private static class ParamExtendedPatch {
        @JsonProperty(value = "connectionString")
        private Option<String> connectionString;

        public ParamExtendedPatch setConnectionString(String connectionString) {
            if (connectionString == null) {
                this.connectionString = Option.empty();
            } else {
                this.connectionString = Option.of(connectionString);
            }
            return this;
        }
    }
}
