package com.sim.socket;

public class Main {

    public static void main(String[] args) {
        FileRequestClient client = new FileRequestClient();

        int loopCount = 1;
        long startTime, endTime;
        
        startTime = System.currentTimeMillis();
        for(int i = 0; i < loopCount; i++) {
            client.sendMessage(true);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Time taken for zero copy: " + (endTime - startTime) + "ms");


        startTime = System.currentTimeMillis();
        for(int i = 0; i < loopCount; i++) {
            client.sendMessage(false);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Time taken for non zero copy: " + (endTime - startTime) + "ms");
    }
}
