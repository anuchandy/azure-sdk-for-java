// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.paloaltonetworks.ngfw.implementation;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.Context;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.paloaltonetworks.ngfw.fluent.CertificateObjectGlobalRulestacksClient;
import com.azure.resourcemanager.paloaltonetworks.ngfw.fluent.models.CertificateObjectGlobalRulestackResourceInner;
import com.azure.resourcemanager.paloaltonetworks.ngfw.models.CertificateObjectGlobalRulestackResource;
import com.azure.resourcemanager.paloaltonetworks.ngfw.models.CertificateObjectGlobalRulestacks;

public final class CertificateObjectGlobalRulestacksImpl implements CertificateObjectGlobalRulestacks {
    private static final ClientLogger LOGGER = new ClientLogger(CertificateObjectGlobalRulestacksImpl.class);

    private final CertificateObjectGlobalRulestacksClient innerClient;

    private final com.azure.resourcemanager.paloaltonetworks.ngfw.PaloAltoNetworksNgfwManager serviceManager;

    public CertificateObjectGlobalRulestacksImpl(
        CertificateObjectGlobalRulestacksClient innerClient,
        com.azure.resourcemanager.paloaltonetworks.ngfw.PaloAltoNetworksNgfwManager serviceManager) {
        this.innerClient = innerClient;
        this.serviceManager = serviceManager;
    }

    public PagedIterable<CertificateObjectGlobalRulestackResource> list(String globalRulestackName) {
        PagedIterable<CertificateObjectGlobalRulestackResourceInner> inner =
            this.serviceClient().list(globalRulestackName);
        return Utils.mapPage(inner, inner1 -> new CertificateObjectGlobalRulestackResourceImpl(inner1, this.manager()));
    }

    public PagedIterable<CertificateObjectGlobalRulestackResource> list(String globalRulestackName, Context context) {
        PagedIterable<CertificateObjectGlobalRulestackResourceInner> inner =
            this.serviceClient().list(globalRulestackName, context);
        return Utils.mapPage(inner, inner1 -> new CertificateObjectGlobalRulestackResourceImpl(inner1, this.manager()));
    }

    public Response<CertificateObjectGlobalRulestackResource> getWithResponse(
        String globalRulestackName, String name, Context context) {
        Response<CertificateObjectGlobalRulestackResourceInner> inner =
            this.serviceClient().getWithResponse(globalRulestackName, name, context);
        if (inner != null) {
            return new SimpleResponse<>(
                inner.getRequest(),
                inner.getStatusCode(),
                inner.getHeaders(),
                new CertificateObjectGlobalRulestackResourceImpl(inner.getValue(), this.manager()));
        } else {
            return null;
        }
    }

    public CertificateObjectGlobalRulestackResource get(String globalRulestackName, String name) {
        CertificateObjectGlobalRulestackResourceInner inner = this.serviceClient().get(globalRulestackName, name);
        if (inner != null) {
            return new CertificateObjectGlobalRulestackResourceImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public CertificateObjectGlobalRulestackResource createOrUpdate(
        String globalRulestackName, String name, CertificateObjectGlobalRulestackResourceInner resource) {
        CertificateObjectGlobalRulestackResourceInner inner =
            this.serviceClient().createOrUpdate(globalRulestackName, name, resource);
        if (inner != null) {
            return new CertificateObjectGlobalRulestackResourceImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public CertificateObjectGlobalRulestackResource createOrUpdate(
        String globalRulestackName,
        String name,
        CertificateObjectGlobalRulestackResourceInner resource,
        Context context) {
        CertificateObjectGlobalRulestackResourceInner inner =
            this.serviceClient().createOrUpdate(globalRulestackName, name, resource, context);
        if (inner != null) {
            return new CertificateObjectGlobalRulestackResourceImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public void deleteByResourceGroup(String globalRulestackName, String name) {
        this.serviceClient().delete(globalRulestackName, name);
    }

    public void delete(String globalRulestackName, String name, Context context) {
        this.serviceClient().delete(globalRulestackName, name, context);
    }

    private CertificateObjectGlobalRulestacksClient serviceClient() {
        return this.innerClient;
    }

    private com.azure.resourcemanager.paloaltonetworks.ngfw.PaloAltoNetworksNgfwManager manager() {
        return this.serviceManager;
    }
}