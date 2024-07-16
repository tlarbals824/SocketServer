package com.sim.socket;

public class LogUtils {

    public static String getCurrentThreadInfo(){
        return "["+Thread.currentThread().getName()+"]";
    }
}
