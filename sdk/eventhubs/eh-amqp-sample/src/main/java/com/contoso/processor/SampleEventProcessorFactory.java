// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.contoso.processor;

import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.eventprocessorhost.PartitionContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory that returns the corresponding {@link SamplePartitionProcessorT2}.
 */
class SampleEventProcessorFactory implements IEventProcessorFactory<SamplePartitionProcessorT1> {
    private final ConcurrentHashMap<String, SamplePartitionProcessorT1> processorMap;

    /**
     * Creates an instance with the available partition processors.
     *
     * @param processorMap The available partition processors to use.
     */
    SampleEventProcessorFactory(ConcurrentHashMap<String, SamplePartitionProcessorT1> processorMap) {
        this.processorMap = processorMap;
    }

    /**
     * Returns a processor that matches the partition id.
     *
     * @param context Context for the partition.
     * @return The processor.
     * @throws RuntimeException if there is no partition processor for the partition id.
     */
    @Override
    public SamplePartitionProcessorT1 createEventProcessor(PartitionContext context) {
        final String partitionId = context.getPartitionId();

        // System.out.printf("Claimed partition: %s%n", partitionId);

        final SamplePartitionProcessorT1 samplePartitionProcessor = processorMap.get(partitionId);
        if (samplePartitionProcessor == null) {
            throw new RuntimeException("There should have been a processor for partition " + partitionId);
        }

        return samplePartitionProcessor;
    }
}

