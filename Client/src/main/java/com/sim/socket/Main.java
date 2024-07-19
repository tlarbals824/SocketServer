package com.sim.socket;

public class Main {

    public static void main(String[] args) {
        FileRequestClient client = new FileRequestClient();

        int loopCount = 1000;
        long startTime, endTime;

        /**
         * loop count: 10000, Average time for zero copy: 0ms
         * Total time for zero copy: 1837ms
         */
        startTime = System.currentTimeMillis();
        for(int i = 0; i < loopCount; i++) {
            client.sendMessage(true);
        }
        endTime = System.currentTimeMillis();
        long averageTime = (endTime - startTime) / loopCount;
        System.out.println("loop count: "+loopCount+", Average time for zero copy: " + averageTime+"ms");
        System.out.println("Total time for zero copy: " + (endTime - startTime)+"ms");

        /**
         * loop count: 10000, Average time for non zero copy: 0ms
         * Total time for non zero copy: 2535ms
         */
        startTime = System.currentTimeMillis();
        for(int i = 0; i < loopCount; i++) {
            client.sendMessage(false);
        }
        endTime = System.currentTimeMillis();
        averageTime = (endTime - startTime) / loopCount;
        System.out.println("loop count: "+loopCount+", Average time for non zero copy: " + averageTime+"ms");
        System.out.println("Total time for non zero copy: " + (endTime - startTime)+"ms");
    }
}