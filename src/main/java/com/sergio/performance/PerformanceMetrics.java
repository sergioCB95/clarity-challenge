package com.sergio.performance;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

public class PerformanceMetrics {

    private static PerformanceMetrics performanceMetrics = null;

    private Instant startExecution;
    private Instant endExecution;

    Runtime runtime = Runtime.getRuntime();

    private PerformanceMetrics() {}

    public static PerformanceMetrics getInstance() {
        if(performanceMetrics == null) {
            performanceMetrics = new PerformanceMetrics();
        }

        return performanceMetrics;
    }

    public void startingExecution() {
        this.startExecution = Instant.now();
    }

    public void endingExecution() {
        this.endExecution = Instant.now();
    }

    public long getCurrentExecutionTime() {
        return Instant.now().toEpochMilli() - this.startExecution.toEpochMilli();
    }

    public long getFinalExecutionTime() {
        return this.endExecution.toEpochMilli() - this.startExecution.toEpochMilli();
    }

    public long getMemoryUsed() {
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);
    }

    public void monitoringMemory(AtomicBoolean stillMonitoring) {
        new Thread(() ->{
            try {
                while(stillMonitoring.get()) {
                    System.out.println(
                            "Memory used in " + this.getCurrentExecutionTime() + "ms is: " + this.getMemoryUsed() + "MB"
                    );
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }).start();
    }
}
