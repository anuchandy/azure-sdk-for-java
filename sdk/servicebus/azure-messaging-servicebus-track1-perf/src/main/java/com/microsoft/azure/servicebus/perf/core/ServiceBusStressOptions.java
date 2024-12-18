// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.servicebus.perf.core;

import com.azure.perf.test.core.PerfStressOptions;
import com.beust.jcommander.Parameter;

/**
 * Represents the command line configurable options for a performance test.
 */
public class ServiceBusStressOptions extends PerfStressOptions {

    @Parameter(names = { "-mr", "--maxReceive" }, description = "MaxReceive messages")
    private int messagesToReceive = 10;

    @Parameter(names = { "-ms", "--messageSend" }, description = "Messages to send")
    private int messagesToSend = 10;

    @Parameter(names = { "-msb", "--messageSizeBytes" }, description = "Size(in bytes) of one Message")
    private int messagesSizeBytesToSend = 10;

    @Parameter(
        names = { "-idm", "--isDeleteMode" },
        description = "Receiver client is receive_and_delete mode or peek_lock mode")
    private boolean isDeleteMode = true;

    /**
     * Get the configured messagesToSend option for performance test.
     * @return The size.
     */
    public int getMessagesToSend() {
        return messagesToSend;
    }

    /**
     * Get the configured messagesToReceive option for performance test.
     * @return The size.
     */
    public int getMessagesToReceive() {
        return messagesToReceive;
    }

    /**
     * Get the configured messagesSizeBytesToSend option for performance test.
     * @return The size.
     */
    public int getMessagesSizeBytesToSend() {
        return messagesSizeBytesToSend;
    }

    /**
     * Get the configured isDeleteMode option for performance test.
     * @return Receive mod is receive_and_delete mode or not.
     */
    public boolean getIsDeleteMode() {
        return isDeleteMode;
    }
}
