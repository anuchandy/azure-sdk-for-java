package com.contoso.processor;

import java.util.List;

public class EventProcessorMain {
    private static String connectionString = System.getenv("CON_STR");
    private static String eventHubName = System.getenv("EH_NAME_PROCESSOR");
    private static String consumerGroupT1 = System.getenv("CG_NAME_PROCESSOR_T1");
    private static String consumerGroupT2 = System.getenv("CG_NAME_PROCESSOR_T2");
    private static String storageUrl = System.getenv("STG_URL_PROCESSOR");
    private static String storageConnectionString = System.getenv("STG_CON_STR_PROCESSOR");
    private static boolean inMemoryState = false;

    private static int preFetch = 100;
    private static int count = 1800;

    public static void run() {
        System.out.println("SDK    Total_Events    Total Duration  Rate_(events/s)");
        RunInfoCallback callback = new RunInfoCallback() {
            @Override
            public void call(String sdk, long totalEvents, double elapsedTime, double eventsPerSeconds, List<String> partitionsStats) {
                System.out.print(String.format("%s \t%d \t%.2f\t%.2f\n", sdk, totalEvents, elapsedTime, eventsPerSeconds));
            }
        };

        for (int i = 0; i < 3; i++) {
            new Track1_EventProcessor(connectionString,
                eventHubName,
                consumerGroupT1,
                preFetch,
                count,
                storageUrl,
                storageConnectionString,
                inMemoryState).run(callback);

            new Track2_EventProcessor(connectionString,
                eventHubName,
                consumerGroupT2,
                preFetch,
                count,
                storageUrl,
                storageConnectionString,
                inMemoryState).run(callback);
        }
    }
}
