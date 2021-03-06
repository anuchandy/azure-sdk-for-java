// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator

package com.microsoft.azure.storage.blob.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.microsoft.rest.v2.DateTimeRfc1123;
import com.microsoft.rest.v2.annotations.HeaderCollection;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Defines headers for GetProperties operation.
 */
@JacksonXmlRootElement(localName = "Container-GetProperties-Headers")
public final class ContainerGetPropertiesHeaders {
    /**
     * The metadata property.
     */
    @HeaderCollection("x-ms-meta-")
    private Map<String, String> metadata;

    /**
     * The ETag contains a value that you can use to perform operations
     * conditionally. If the request version is 2011-08-18 or newer, the ETag
     * value will be in quotes.
     */
    @JsonProperty(value = "ETag")
    private String eTag;

    /**
     * Returns the date and time the container was last modified. Any operation
     * that modifies the blob, including an update of the blob's metadata or
     * properties, changes the last-modified time of the blob.
     */
    @JsonProperty(value = "Last-Modified")
    private DateTimeRfc1123 lastModified;

    /**
     * When a blob is leased, specifies whether the lease is of infinite or
     * fixed duration. Possible values include: 'infinite', 'fixed'.
     */
    @JsonProperty(value = "x-ms-lease-duration")
    private LeaseDurationType leaseDuration;

    /**
     * Lease state of the blob. Possible values include: 'available', 'leased',
     * 'expired', 'breaking', 'broken'.
     */
    @JsonProperty(value = "x-ms-lease-state")
    private LeaseStateType leaseState;

    /**
     * The current lease status of the blob. Possible values include: 'locked',
     * 'unlocked'.
     */
    @JsonProperty(value = "x-ms-lease-status")
    private LeaseStatusType leaseStatus;

    /**
     * This header uniquely identifies the request that was made and can be
     * used for troubleshooting the request.
     */
    @JsonProperty(value = "x-ms-request-id")
    private String requestId;

    /**
     * Indicates the version of the Blob service used to execute the request.
     * This header is returned for requests made against version 2009-09-19 and
     * above.
     */
    @JsonProperty(value = "x-ms-version")
    private String version;

    /**
     * UTC date/time value generated by the service that indicates the time at
     * which the response was initiated.
     */
    @JsonProperty(value = "Date")
    private DateTimeRfc1123 date;

    /**
     * Indicated whether data in the container may be accessed publicly and the
     * level of access. Possible values include: 'container', 'blob'.
     */
    @JsonProperty(value = "x-ms-blob-public-access")
    private PublicAccessType blobPublicAccess;

    /**
     * Indicates whether the container has an immutability policy set on it.
     */
    @JsonProperty(value = "x-ms-has-immutability-policy")
    private Boolean hasImmutabilityPolicy;

    /**
     * Indicates whether the container has a legal hold.
     */
    @JsonProperty(value = "x-ms-has-legal-hold")
    private Boolean hasLegalHold;

    /**
     * Get the metadata value.
     *
     * @return the metadata value.
     */
    public Map<String, String> metadata() {
        return this.metadata;
    }

    /**
     * Set the metadata value.
     *
     * @param metadata the metadata value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Get the eTag value.
     *
     * @return the eTag value.
     */
    public String eTag() {
        return this.eTag;
    }

    /**
     * Set the eTag value.
     *
     * @param eTag the eTag value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withETag(String eTag) {
        this.eTag = eTag;
        return this;
    }

    /**
     * Get the lastModified value.
     *
     * @return the lastModified value.
     */
    public OffsetDateTime lastModified() {
        if (this.lastModified == null) {
            return null;
        }
        return this.lastModified.dateTime();
    }

    /**
     * Set the lastModified value.
     *
     * @param lastModified the lastModified value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withLastModified(OffsetDateTime lastModified) {
        if (lastModified == null) {
            this.lastModified = null;
        } else {
            this.lastModified = new DateTimeRfc1123(lastModified);
        }
        return this;
    }

    /**
     * Get the leaseDuration value.
     *
     * @return the leaseDuration value.
     */
    public LeaseDurationType leaseDuration() {
        return this.leaseDuration;
    }

    /**
     * Set the leaseDuration value.
     *
     * @param leaseDuration the leaseDuration value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withLeaseDuration(LeaseDurationType leaseDuration) {
        this.leaseDuration = leaseDuration;
        return this;
    }

    /**
     * Get the leaseState value.
     *
     * @return the leaseState value.
     */
    public LeaseStateType leaseState() {
        return this.leaseState;
    }

    /**
     * Set the leaseState value.
     *
     * @param leaseState the leaseState value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withLeaseState(LeaseStateType leaseState) {
        this.leaseState = leaseState;
        return this;
    }

    /**
     * Get the leaseStatus value.
     *
     * @return the leaseStatus value.
     */
    public LeaseStatusType leaseStatus() {
        return this.leaseStatus;
    }

    /**
     * Set the leaseStatus value.
     *
     * @param leaseStatus the leaseStatus value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withLeaseStatus(LeaseStatusType leaseStatus) {
        this.leaseStatus = leaseStatus;
        return this;
    }

    /**
     * Get the requestId value.
     *
     * @return the requestId value.
     */
    public String requestId() {
        return this.requestId;
    }

    /**
     * Set the requestId value.
     *
     * @param requestId the requestId value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * Get the version value.
     *
     * @return the version value.
     */
    public String version() {
        return this.version;
    }

    /**
     * Set the version value.
     *
     * @param version the version value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Get the date value.
     *
     * @return the date value.
     */
    public OffsetDateTime date() {
        if (this.date == null) {
            return null;
        }
        return this.date.dateTime();
    }

    /**
     * Set the date value.
     *
     * @param date the date value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withDate(OffsetDateTime date) {
        if (date == null) {
            this.date = null;
        } else {
            this.date = new DateTimeRfc1123(date);
        }
        return this;
    }

    /**
     * Get the blobPublicAccess value.
     *
     * @return the blobPublicAccess value.
     */
    public PublicAccessType blobPublicAccess() {
        return this.blobPublicAccess;
    }

    /**
     * Set the blobPublicAccess value.
     *
     * @param blobPublicAccess the blobPublicAccess value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withBlobPublicAccess(PublicAccessType blobPublicAccess) {
        this.blobPublicAccess = blobPublicAccess;
        return this;
    }

    /**
     * Get the hasImmutabilityPolicy value.
     *
     * @return the hasImmutabilityPolicy value.
     */
    public Boolean hasImmutabilityPolicy() {
        return this.hasImmutabilityPolicy;
    }

    /**
     * Set the hasImmutabilityPolicy value.
     *
     * @param hasImmutabilityPolicy the hasImmutabilityPolicy value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withHasImmutabilityPolicy(Boolean hasImmutabilityPolicy) {
        this.hasImmutabilityPolicy = hasImmutabilityPolicy;
        return this;
    }

    /**
     * Get the hasLegalHold value.
     *
     * @return the hasLegalHold value.
     */
    public Boolean hasLegalHold() {
        return this.hasLegalHold;
    }

    /**
     * Set the hasLegalHold value.
     *
     * @param hasLegalHold the hasLegalHold value to set.
     * @return the ContainerGetPropertiesHeaders object itself.
     */
    public ContainerGetPropertiesHeaders withHasLegalHold(Boolean hasLegalHold) {
        this.hasLegalHold = hasLegalHold;
        return this;
    }
}
