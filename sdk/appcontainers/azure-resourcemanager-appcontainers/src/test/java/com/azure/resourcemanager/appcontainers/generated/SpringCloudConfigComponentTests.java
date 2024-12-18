// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.appcontainers.models.JavaComponentConfigurationProperty;
import com.azure.resourcemanager.appcontainers.models.JavaComponentPropertiesScale;
import com.azure.resourcemanager.appcontainers.models.JavaComponentServiceBind;
import com.azure.resourcemanager.appcontainers.models.SpringCloudConfigComponent;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class SpringCloudConfigComponentTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        SpringCloudConfigComponent model = BinaryData.fromString(
            "{\"componentType\":\"SpringCloudConfig\",\"provisioningState\":\"InProgress\",\"configurations\":[{\"propertyName\":\"ekraokqkbudbt\",\"value\":\"okbavlyttaak\"},{\"propertyName\":\"frkebsmhpd\",\"value\":\"dig\"},{\"propertyName\":\"olekscbctnanqim\",\"value\":\"zxpdcldpkawnsnl\"},{\"propertyName\":\"mouxwksqmudmfco\",\"value\":\"icziuswswjrkb\"}],\"scale\":{\"minReplicas\":1858823351,\"maxReplicas\":1182597783},\"serviceBinds\":[{\"name\":\"yfscyrfwbivqvo\",\"serviceId\":\"uyzwvbhlimbyqecr\"}]}")
            .toObject(SpringCloudConfigComponent.class);
        Assertions.assertEquals("ekraokqkbudbt", model.configurations().get(0).propertyName());
        Assertions.assertEquals("okbavlyttaak", model.configurations().get(0).value());
        Assertions.assertEquals(1858823351, model.scale().minReplicas());
        Assertions.assertEquals(1182597783, model.scale().maxReplicas());
        Assertions.assertEquals("yfscyrfwbivqvo", model.serviceBinds().get(0).name());
        Assertions.assertEquals("uyzwvbhlimbyqecr", model.serviceBinds().get(0).serviceId());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        SpringCloudConfigComponent model = new SpringCloudConfigComponent().withConfigurations(Arrays.asList(
            new JavaComponentConfigurationProperty().withPropertyName("ekraokqkbudbt").withValue("okbavlyttaak"),
            new JavaComponentConfigurationProperty().withPropertyName("frkebsmhpd").withValue("dig"),
            new JavaComponentConfigurationProperty().withPropertyName("olekscbctnanqim").withValue("zxpdcldpkawnsnl"),
            new JavaComponentConfigurationProperty().withPropertyName("mouxwksqmudmfco").withValue("icziuswswjrkb")))
            .withScale(new JavaComponentPropertiesScale().withMinReplicas(1858823351).withMaxReplicas(1182597783))
            .withServiceBinds(Arrays
                .asList(new JavaComponentServiceBind().withName("yfscyrfwbivqvo").withServiceId("uyzwvbhlimbyqecr")));
        model = BinaryData.fromObject(model).toObject(SpringCloudConfigComponent.class);
        Assertions.assertEquals("ekraokqkbudbt", model.configurations().get(0).propertyName());
        Assertions.assertEquals("okbavlyttaak", model.configurations().get(0).value());
        Assertions.assertEquals(1858823351, model.scale().minReplicas());
        Assertions.assertEquals(1182597783, model.scale().maxReplicas());
        Assertions.assertEquals("yfscyrfwbivqvo", model.serviceBinds().get(0).name());
        Assertions.assertEquals("uyzwvbhlimbyqecr", model.serviceBinds().get(0).serviceId());
    }
}
