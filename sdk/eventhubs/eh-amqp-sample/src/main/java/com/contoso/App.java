package com.contoso;

public class App {
    private static String connectionString = System.getenv("CON_STR");
    private static String eventHubName = System.getenv("EH_NAME");
    private static String consumerGroup = System.getenv("CG_NAME");
    private static int preFetch = 100;
    private static String partitionId = "0";
    private static int count = 1800;

    public static void main( String[] args ) {
        System.out.println( "Start--" );
        //Track1.run(connectionString, eventHubName, consumerGroup, partitionId, preFetch, count);
        Track2.run(connectionString, eventHubName, consumerGroup, partitionId, preFetch, count);
        System.out.println( "End--" );
    }
}
