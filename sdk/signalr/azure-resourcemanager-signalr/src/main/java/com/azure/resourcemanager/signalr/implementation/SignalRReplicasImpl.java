// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.signalr.implementation;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.Context;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.signalr.fluent.SignalRReplicasClient;
import com.azure.resourcemanager.signalr.fluent.models.ReplicaInner;
import com.azure.resourcemanager.signalr.models.Replica;
import com.azure.resourcemanager.signalr.models.SignalRReplicas;

public final class SignalRReplicasImpl implements SignalRReplicas {
    private static final ClientLogger LOGGER = new ClientLogger(SignalRReplicasImpl.class);

    private final SignalRReplicasClient innerClient;

    private final com.azure.resourcemanager.signalr.SignalRManager serviceManager;

    public SignalRReplicasImpl(SignalRReplicasClient innerClient,
        com.azure.resourcemanager.signalr.SignalRManager serviceManager) {
        this.innerClient = innerClient;
        this.serviceManager = serviceManager;
    }

    public PagedIterable<Replica> list(String resourceGroupName, String resourceName) {
        PagedIterable<ReplicaInner> inner = this.serviceClient().list(resourceGroupName, resourceName);
        return Utils.mapPage(inner, inner1 -> new ReplicaImpl(inner1, this.manager()));
    }

    public PagedIterable<Replica> list(String resourceGroupName, String resourceName, Context context) {
        PagedIterable<ReplicaInner> inner = this.serviceClient().list(resourceGroupName, resourceName, context);
        return Utils.mapPage(inner, inner1 -> new ReplicaImpl(inner1, this.manager()));
    }

    public Response<Replica> getWithResponse(String resourceGroupName, String resourceName, String replicaName,
        Context context) {
        Response<ReplicaInner> inner
            = this.serviceClient().getWithResponse(resourceGroupName, resourceName, replicaName, context);
        if (inner != null) {
            return new SimpleResponse<>(inner.getRequest(), inner.getStatusCode(), inner.getHeaders(),
                new ReplicaImpl(inner.getValue(), this.manager()));
        } else {
            return null;
        }
    }

    public Replica get(String resourceGroupName, String resourceName, String replicaName) {
        ReplicaInner inner = this.serviceClient().get(resourceGroupName, resourceName, replicaName);
        if (inner != null) {
            return new ReplicaImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public Response<Void> deleteWithResponse(String resourceGroupName, String resourceName, String replicaName,
        Context context) {
        return this.serviceClient().deleteWithResponse(resourceGroupName, resourceName, replicaName, context);
    }

    public void delete(String resourceGroupName, String resourceName, String replicaName) {
        this.serviceClient().delete(resourceGroupName, resourceName, replicaName);
    }

    public void restart(String resourceGroupName, String resourceName, String replicaName) {
        this.serviceClient().restart(resourceGroupName, resourceName, replicaName);
    }

    public void restart(String resourceGroupName, String resourceName, String replicaName, Context context) {
        this.serviceClient().restart(resourceGroupName, resourceName, replicaName, context);
    }

    public Replica getById(String id) {
        String resourceGroupName = Utils.getValueFromIdByName(id, "resourceGroups");
        if (resourceGroupName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'resourceGroups'.", id)));
        }
        String resourceName = Utils.getValueFromIdByName(id, "signalR");
        if (resourceName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'signalR'.", id)));
        }
        String replicaName = Utils.getValueFromIdByName(id, "replicas");
        if (replicaName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'replicas'.", id)));
        }
        return this.getWithResponse(resourceGroupName, resourceName, replicaName, Context.NONE).getValue();
    }

    public Response<Replica> getByIdWithResponse(String id, Context context) {
        String resourceGroupName = Utils.getValueFromIdByName(id, "resourceGroups");
        if (resourceGroupName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'resourceGroups'.", id)));
        }
        String resourceName = Utils.getValueFromIdByName(id, "signalR");
        if (resourceName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'signalR'.", id)));
        }
        String replicaName = Utils.getValueFromIdByName(id, "replicas");
        if (replicaName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'replicas'.", id)));
        }
        return this.getWithResponse(resourceGroupName, resourceName, replicaName, context);
    }

    public void deleteById(String id) {
        String resourceGroupName = Utils.getValueFromIdByName(id, "resourceGroups");
        if (resourceGroupName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'resourceGroups'.", id)));
        }
        String resourceName = Utils.getValueFromIdByName(id, "signalR");
        if (resourceName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'signalR'.", id)));
        }
        String replicaName = Utils.getValueFromIdByName(id, "replicas");
        if (replicaName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'replicas'.", id)));
        }
        this.deleteWithResponse(resourceGroupName, resourceName, replicaName, Context.NONE);
    }

    public Response<Void> deleteByIdWithResponse(String id, Context context) {
        String resourceGroupName = Utils.getValueFromIdByName(id, "resourceGroups");
        if (resourceGroupName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'resourceGroups'.", id)));
        }
        String resourceName = Utils.getValueFromIdByName(id, "signalR");
        if (resourceName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'signalR'.", id)));
        }
        String replicaName = Utils.getValueFromIdByName(id, "replicas");
        if (replicaName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'replicas'.", id)));
        }
        return this.deleteWithResponse(resourceGroupName, resourceName, replicaName, context);
    }

    private SignalRReplicasClient serviceClient() {
        return this.innerClient;
    }

    private com.azure.resourcemanager.signalr.SignalRManager manager() {
        return this.serviceManager;
    }

    public ReplicaImpl define(String name) {
        return new ReplicaImpl(name, this.manager());
    }
}
