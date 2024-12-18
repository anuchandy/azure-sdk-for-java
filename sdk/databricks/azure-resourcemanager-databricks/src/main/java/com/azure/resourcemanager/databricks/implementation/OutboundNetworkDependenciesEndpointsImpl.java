// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.databricks.implementation;

import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.Context;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.databricks.fluent.OutboundNetworkDependenciesEndpointsClient;
import com.azure.resourcemanager.databricks.fluent.models.OutboundEnvironmentEndpointInner;
import com.azure.resourcemanager.databricks.models.OutboundEnvironmentEndpoint;
import com.azure.resourcemanager.databricks.models.OutboundNetworkDependenciesEndpoints;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class OutboundNetworkDependenciesEndpointsImpl implements OutboundNetworkDependenciesEndpoints {
    private static final ClientLogger LOGGER = new ClientLogger(OutboundNetworkDependenciesEndpointsImpl.class);

    private final OutboundNetworkDependenciesEndpointsClient innerClient;

    private final com.azure.resourcemanager.databricks.AzureDatabricksManager serviceManager;

    public OutboundNetworkDependenciesEndpointsImpl(OutboundNetworkDependenciesEndpointsClient innerClient,
        com.azure.resourcemanager.databricks.AzureDatabricksManager serviceManager) {
        this.innerClient = innerClient;
        this.serviceManager = serviceManager;
    }

    public Response<List<OutboundEnvironmentEndpoint>> listWithResponse(String resourceGroupName, String workspaceName,
        Context context) {
        Response<List<OutboundEnvironmentEndpointInner>> inner
            = this.serviceClient().listWithResponse(resourceGroupName, workspaceName, context);
        if (inner != null) {
            return new SimpleResponse<>(inner.getRequest(), inner.getStatusCode(), inner.getHeaders(),
                inner.getValue()
                    .stream()
                    .map(inner1 -> new OutboundEnvironmentEndpointImpl(inner1, this.manager()))
                    .collect(Collectors.toList()));
        } else {
            return null;
        }
    }

    public List<OutboundEnvironmentEndpoint> list(String resourceGroupName, String workspaceName) {
        List<OutboundEnvironmentEndpointInner> inner = this.serviceClient().list(resourceGroupName, workspaceName);
        if (inner != null) {
            return Collections.unmodifiableList(inner.stream()
                .map(inner1 -> new OutboundEnvironmentEndpointImpl(inner1, this.manager()))
                .collect(Collectors.toList()));
        } else {
            return Collections.emptyList();
        }
    }

    private OutboundNetworkDependenciesEndpointsClient serviceClient() {
        return this.innerClient;
    }

    private com.azure.resourcemanager.databricks.AzureDatabricksManager manager() {
        return this.serviceManager;
    }
}
