package com.contoso.receiver;

import com.contoso.receiver.Track1_ReceiveFromPartition;
import com.contoso.receiver.Track2_ReceiveFromPartition;

public class ReceiveFromPartitionMain {
    private static String connectionString = System.getenv("CON_STR");
    private static String eventHubName = System.getenv("EH_NAME");
    private static String consumerGroup = System.getenv("CG_NAME");
    private static int preFetch = 100;
    private static String partitionId = "0";
    private static int count = 1800;

    public static void run() {
        for (int i = 0; i < 1; i++) {
            System.out.println("TRACK_1: Start--");
            new Track1_ReceiveFromPartition(connectionString,
                eventHubName,
                consumerGroup,
                partitionId,
                preFetch,
                count).run();
            System.out.println("TRACK_1: End--");

            System.out.println();

            System.out.println("TRACK_2: Start--");
            new Track2_ReceiveFromPartition(connectionString,
                eventHubName,
                consumerGroup,
                partitionId,
                preFetch,
                count).run();
            System.out.println("TRACK_2: End--");

            System.out.println();
        }
    }
}
