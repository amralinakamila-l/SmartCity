package org.example.metrics;

public class Metrics {
    private long startTime;
    private long endTime;
    private long operations = 0;

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
    }

    public void inc(String what) {
        operations++;
        // можно при желании логировать:
        // System.out.println("Increment: " + what);
    }

    public void print(String algorithmName) {
        System.out.println(algorithmName + " completed in " + getElapsedMillis() + " ms, operations = " + operations);
    }

    public long getElapsedMillis() {
        return (endTime - startTime) / 1_000_000;
    }
}
