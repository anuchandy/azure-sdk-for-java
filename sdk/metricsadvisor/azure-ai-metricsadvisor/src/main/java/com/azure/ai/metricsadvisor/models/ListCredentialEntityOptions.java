// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.metricsadvisor.models;

import com.azure.core.annotation.Fluent;

/**
 * Additional properties for filtering results on the listCredentialEntities operation.
 */
@Fluent
public final class ListCredentialEntityOptions {
    private Integer maxPage;
    private Integer skip;

    /**
     * Gets limit indicating the number of items that will be included in a service returned page.
     *
     * @return The top value.
     */
    public Integer getMaxPage() {
        return maxPage;
    }

    /**
     * Sets limit indicating the number of items to be included in a service returned page.
     *
     * @param maxPage The top value.
     *
     * @return The ListDataFeedOptions object itself.
     */
    public ListCredentialEntityOptions setMaxPage(final int maxPage) {
        this.maxPage = maxPage;
        return this;
    }

    /**
     * Gets the number of items in the queried collection that will be skipped and not included
     * in the returned result.
     *
     * @return The skip value.
     */
    public Integer getSkip() {
        return skip;
    }

    /**
     * Sets the number of items in the queried collection that are to be skipped and not included
     * in the returned result.
     *
     * @param skip The skip value.
     *
     * @return ListDataFeedOptions itself.
     */
    public ListCredentialEntityOptions setSkip(final int skip) {
        this.skip = skip;
        return this;
    }
}
