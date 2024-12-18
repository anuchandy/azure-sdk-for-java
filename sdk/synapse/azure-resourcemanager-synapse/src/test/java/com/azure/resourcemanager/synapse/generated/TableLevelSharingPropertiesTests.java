// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.synapse.models.TableLevelSharingProperties;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class TableLevelSharingPropertiesTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        TableLevelSharingProperties model = BinaryData.fromString(
            "{\"tablesToInclude\":[\"zgwldoychillcec\",\"ehuwaoa\",\"uhicqllizstacsjv\"],\"tablesToExclude\":[\"eftkwqe\",\"pmvssehaep\"],\"externalTablesToInclude\":[\"cxtczhupeukn\",\"jduyyespydjfb\"],\"externalTablesToExclude\":[\"v\"],\"materializedViewsToInclude\":[\"lrtywikdmhlakuf\",\"gbhgau\",\"cdixmx\"],\"materializedViewsToExclude\":[\"sryjqgdkfno\"]}")
            .toObject(TableLevelSharingProperties.class);
        Assertions.assertEquals("zgwldoychillcec", model.tablesToInclude().get(0));
        Assertions.assertEquals("eftkwqe", model.tablesToExclude().get(0));
        Assertions.assertEquals("cxtczhupeukn", model.externalTablesToInclude().get(0));
        Assertions.assertEquals("v", model.externalTablesToExclude().get(0));
        Assertions.assertEquals("lrtywikdmhlakuf", model.materializedViewsToInclude().get(0));
        Assertions.assertEquals("sryjqgdkfno", model.materializedViewsToExclude().get(0));
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        TableLevelSharingProperties model = new TableLevelSharingProperties()
            .withTablesToInclude(Arrays.asList("zgwldoychillcec", "ehuwaoa", "uhicqllizstacsjv"))
            .withTablesToExclude(Arrays.asList("eftkwqe", "pmvssehaep"))
            .withExternalTablesToInclude(Arrays.asList("cxtczhupeukn", "jduyyespydjfb"))
            .withExternalTablesToExclude(Arrays.asList("v"))
            .withMaterializedViewsToInclude(Arrays.asList("lrtywikdmhlakuf", "gbhgau", "cdixmx"))
            .withMaterializedViewsToExclude(Arrays.asList("sryjqgdkfno"));
        model = BinaryData.fromObject(model).toObject(TableLevelSharingProperties.class);
        Assertions.assertEquals("zgwldoychillcec", model.tablesToInclude().get(0));
        Assertions.assertEquals("eftkwqe", model.tablesToExclude().get(0));
        Assertions.assertEquals("cxtczhupeukn", model.externalTablesToInclude().get(0));
        Assertions.assertEquals("v", model.externalTablesToExclude().get(0));
        Assertions.assertEquals("lrtywikdmhlakuf", model.materializedViewsToInclude().get(0));
        Assertions.assertEquals("sryjqgdkfno", model.materializedViewsToExclude().get(0));
    }
}
