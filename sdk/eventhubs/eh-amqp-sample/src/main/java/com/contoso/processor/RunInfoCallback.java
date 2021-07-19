package com.contoso.processor;

import java.util.List;

@FunctionalInterface
public interface RunInfoCallback {
    public void call(String sdk,
                     long totalEvents,
                     double elapsedTime,
                     double eventsPerSeconds,
                     List<String> partitionsStats);
}
