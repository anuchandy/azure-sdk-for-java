// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.securityinsights.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.securityinsights.models.ThreatIntelligenceAlertRule;
import org.junit.jupiter.api.Assertions;

public final class ThreatIntelligenceAlertRuleTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ThreatIntelligenceAlertRule model = BinaryData.fromString(
            "{\"kind\":\"ThreatIntelligence\",\"properties\":{\"alertRuleTemplateName\":\"hkhg\",\"description\":\"xuwwkp\",\"displayName\":\"efsbzxlbz\",\"enabled\":false,\"lastModifiedUtc\":\"2021-05-31T11:00:02Z\",\"severity\":\"High\",\"tactics\":[\"Impact\",\"CredentialAccess\",\"ResourceDevelopment\",\"Discovery\"],\"techniques\":[\"mwpfs\",\"qtaazyqbxy\"]},\"etag\":\"yfp\",\"id\":\"qi\",\"name\":\"ezxlhdjzqdca\",\"type\":\"wvpsoz\"}")
            .toObject(ThreatIntelligenceAlertRule.class);
        Assertions.assertEquals("yfp", model.etag());
        Assertions.assertEquals("hkhg", model.alertRuleTemplateName());
        Assertions.assertEquals(false, model.enabled());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ThreatIntelligenceAlertRule model
            = new ThreatIntelligenceAlertRule().withEtag("yfp").withAlertRuleTemplateName("hkhg").withEnabled(false);
        model = BinaryData.fromObject(model).toObject(ThreatIntelligenceAlertRule.class);
        Assertions.assertEquals("yfp", model.etag());
        Assertions.assertEquals("hkhg", model.alertRuleTemplateName());
        Assertions.assertEquals(false, model.enabled());
    }
}
