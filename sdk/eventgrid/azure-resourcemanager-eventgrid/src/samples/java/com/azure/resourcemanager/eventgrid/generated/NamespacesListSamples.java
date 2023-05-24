// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.eventgrid.generated;

/** Samples for Namespaces List. */
public final class NamespacesListSamples {
    /*
     * x-ms-original-file: specification/eventgrid/resource-manager/Microsoft.EventGrid/preview/2023-06-01-preview/examples/Namespaces_ListBySubscription.json
     */
    /**
     * Sample code: Namespaces_ListBySubscription.
     *
     * @param manager Entry point to EventGridManager.
     */
    public static void namespacesListBySubscription(com.azure.resourcemanager.eventgrid.EventGridManager manager) {
        manager.namespaces().list(null, null, com.azure.core.util.Context.NONE);
    }
}