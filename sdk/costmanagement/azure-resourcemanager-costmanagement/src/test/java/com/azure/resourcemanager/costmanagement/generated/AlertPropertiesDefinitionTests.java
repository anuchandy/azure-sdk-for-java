// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.costmanagement.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.costmanagement.models.AlertCategory;
import com.azure.resourcemanager.costmanagement.models.AlertCriteria;
import com.azure.resourcemanager.costmanagement.models.AlertPropertiesDefinition;
import com.azure.resourcemanager.costmanagement.models.AlertType;
import org.junit.jupiter.api.Assertions;

public final class AlertPropertiesDefinitionTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        AlertPropertiesDefinition model
            = BinaryData.fromString("{\"type\":\"Budget\",\"category\":\"System\",\"criteria\":\"MultiCurrency\"}")
                .toObject(AlertPropertiesDefinition.class);
        Assertions.assertEquals(AlertType.BUDGET, model.type());
        Assertions.assertEquals(AlertCategory.SYSTEM, model.category());
        Assertions.assertEquals(AlertCriteria.MULTI_CURRENCY, model.criteria());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        AlertPropertiesDefinition model = new AlertPropertiesDefinition().withType(AlertType.BUDGET)
            .withCategory(AlertCategory.SYSTEM)
            .withCriteria(AlertCriteria.MULTI_CURRENCY);
        model = BinaryData.fromObject(model).toObject(AlertPropertiesDefinition.class);
        Assertions.assertEquals(AlertType.BUDGET, model.type());
        Assertions.assertEquals(AlertCategory.SYSTEM, model.category());
        Assertions.assertEquals(AlertCriteria.MULTI_CURRENCY, model.criteria());
    }
}
