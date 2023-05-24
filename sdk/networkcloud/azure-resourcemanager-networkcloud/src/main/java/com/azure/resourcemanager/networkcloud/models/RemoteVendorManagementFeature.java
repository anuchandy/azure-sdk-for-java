// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.networkcloud.models;

import com.azure.core.util.ExpandableStringEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Collection;

/** The indicator of whether the storage appliance supports remote vendor management. */
public final class RemoteVendorManagementFeature extends ExpandableStringEnum<RemoteVendorManagementFeature> {
    /** Static value Supported for RemoteVendorManagementFeature. */
    public static final RemoteVendorManagementFeature SUPPORTED = fromString("Supported");

    /** Static value Unsupported for RemoteVendorManagementFeature. */
    public static final RemoteVendorManagementFeature UNSUPPORTED = fromString("Unsupported");

    /**
     * Creates a new instance of RemoteVendorManagementFeature value.
     *
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Deprecated
    public RemoteVendorManagementFeature() {
    }

    /**
     * Creates or finds a RemoteVendorManagementFeature from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding RemoteVendorManagementFeature.
     */
    @JsonCreator
    public static RemoteVendorManagementFeature fromString(String name) {
        return fromString(name, RemoteVendorManagementFeature.class);
    }

    /**
     * Gets known RemoteVendorManagementFeature values.
     *
     * @return known RemoteVendorManagementFeature values.
     */
    public static Collection<RemoteVendorManagementFeature> values() {
        return values(RemoteVendorManagementFeature.class);
    }
}