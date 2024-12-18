// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.streamanalytics.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.streamanalytics.models.CompatibilityLevel;
import com.azure.resourcemanager.streamanalytics.models.CompileQuery;
import com.azure.resourcemanager.streamanalytics.models.FunctionInput;
import com.azure.resourcemanager.streamanalytics.models.FunctionOutput;
import com.azure.resourcemanager.streamanalytics.models.JobType;
import com.azure.resourcemanager.streamanalytics.models.QueryFunction;
import com.azure.resourcemanager.streamanalytics.models.QueryInput;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class CompileQueryTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        CompileQuery model = BinaryData.fromString(
            "{\"query\":\"iqzbq\",\"inputs\":[{\"name\":\"ovm\",\"type\":\"okacspk\"},{\"name\":\"lhzdobp\",\"type\":\"jmflbvvnch\"},{\"name\":\"kcciwwzjuqkhr\",\"type\":\"ajiwkuo\"},{\"name\":\"oskg\",\"type\":\"sauuimj\"}],\"functions\":[{\"name\":\"ied\",\"type\":\"ugidyjrr\",\"bindingType\":\"byao\",\"inputs\":[{\"dataType\":\"xc\",\"isConfigurationParameter\":false}],\"output\":{\"dataType\":\"clhocohsl\"}},{\"name\":\"ev\",\"type\":\"eggzfb\",\"bindingType\":\"hfmvfaxkffe\",\"inputs\":[{\"dataType\":\"hl\",\"isConfigurationParameter\":false},{\"dataType\":\"yvshxmz\",\"isConfigurationParameter\":true},{\"dataType\":\"oggigrxwburv\",\"isConfigurationParameter\":false},{\"dataType\":\"nspydptkoenkoukn\",\"isConfigurationParameter\":true}],\"output\":{\"dataType\":\"tiukbldngkpoci\"}},{\"name\":\"azyxoegukg\",\"type\":\"npiucgygevqznty\",\"bindingType\":\"mrbpizcdrqj\",\"inputs\":[{\"dataType\":\"ydnfyhxdeoejz\",\"isConfigurationParameter\":false},{\"dataType\":\"fsj\",\"isConfigurationParameter\":true}],\"output\":{\"dataType\":\"fbishcbkha\"}},{\"name\":\"deyeamdphagalpbu\",\"type\":\"wgipwhono\",\"bindingType\":\"kgshwa\",\"inputs\":[{\"dataType\":\"xzbinjeputt\",\"isConfigurationParameter\":false}],\"output\":{\"dataType\":\"nuzo\"}}],\"jobType\":\"Edge\",\"compatibilityLevel\":\"1.0\"}")
            .toObject(CompileQuery.class);
        Assertions.assertEquals("iqzbq", model.query());
        Assertions.assertEquals("ovm", model.inputs().get(0).name());
        Assertions.assertEquals("okacspk", model.inputs().get(0).type());
        Assertions.assertEquals("ied", model.functions().get(0).name());
        Assertions.assertEquals("ugidyjrr", model.functions().get(0).type());
        Assertions.assertEquals("byao", model.functions().get(0).bindingType());
        Assertions.assertEquals("xc", model.functions().get(0).inputs().get(0).dataType());
        Assertions.assertEquals(false, model.functions().get(0).inputs().get(0).isConfigurationParameter());
        Assertions.assertEquals("clhocohsl", model.functions().get(0).output().dataType());
        Assertions.assertEquals(JobType.EDGE, model.jobType());
        Assertions.assertEquals(CompatibilityLevel.ONE_ZERO, model.compatibilityLevel());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        CompileQuery model = new CompileQuery().withQuery("iqzbq")
            .withInputs(Arrays.asList(new QueryInput().withName("ovm").withType("okacspk"),
                new QueryInput().withName("lhzdobp").withType("jmflbvvnch"),
                new QueryInput().withName("kcciwwzjuqkhr").withType("ajiwkuo"),
                new QueryInput().withName("oskg").withType("sauuimj")))
            .withFunctions(
                Arrays
                    .asList(
                        new QueryFunction().withName("ied")
                            .withType("ugidyjrr")
                            .withBindingType("byao")
                            .withInputs(Arrays
                                .asList(new FunctionInput().withDataType("xc").withIsConfigurationParameter(false)))
                            .withOutput(new FunctionOutput().withDataType("clhocohsl")),
                        new QueryFunction().withName("ev")
                            .withType("eggzfb")
                            .withBindingType("hfmvfaxkffe")
                            .withInputs(Arrays.asList(
                                new FunctionInput().withDataType("hl").withIsConfigurationParameter(false),
                                new FunctionInput().withDataType("yvshxmz").withIsConfigurationParameter(true),
                                new FunctionInput().withDataType("oggigrxwburv").withIsConfigurationParameter(false),
                                new FunctionInput().withDataType("nspydptkoenkoukn")
                                    .withIsConfigurationParameter(true)))
                            .withOutput(new FunctionOutput().withDataType("tiukbldngkpoci")),
                        new QueryFunction().withName("azyxoegukg")
                            .withType("npiucgygevqznty")
                            .withBindingType("mrbpizcdrqj")
                            .withInputs(Arrays.asList(
                                new FunctionInput().withDataType("ydnfyhxdeoejz").withIsConfigurationParameter(false),
                                new FunctionInput().withDataType("fsj").withIsConfigurationParameter(true)))
                            .withOutput(new FunctionOutput().withDataType("fbishcbkha")),
                        new QueryFunction().withName("deyeamdphagalpbu")
                            .withType("wgipwhono")
                            .withBindingType("kgshwa")
                            .withInputs(Arrays.asList(
                                new FunctionInput().withDataType("xzbinjeputt").withIsConfigurationParameter(false)))
                            .withOutput(new FunctionOutput().withDataType("nuzo"))))
            .withJobType(JobType.EDGE)
            .withCompatibilityLevel(CompatibilityLevel.ONE_ZERO);
        model = BinaryData.fromObject(model).toObject(CompileQuery.class);
        Assertions.assertEquals("iqzbq", model.query());
        Assertions.assertEquals("ovm", model.inputs().get(0).name());
        Assertions.assertEquals("okacspk", model.inputs().get(0).type());
        Assertions.assertEquals("ied", model.functions().get(0).name());
        Assertions.assertEquals("ugidyjrr", model.functions().get(0).type());
        Assertions.assertEquals("byao", model.functions().get(0).bindingType());
        Assertions.assertEquals("xc", model.functions().get(0).inputs().get(0).dataType());
        Assertions.assertEquals(false, model.functions().get(0).inputs().get(0).isConfigurationParameter());
        Assertions.assertEquals("clhocohsl", model.functions().get(0).output().dataType());
        Assertions.assertEquals(JobType.EDGE, model.jobType());
        Assertions.assertEquals(CompatibilityLevel.ONE_ZERO, model.compatibilityLevel());
    }
}
