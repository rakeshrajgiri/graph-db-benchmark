package org.benchmark;

public class ConcurrentResult {

    private int threads;
    private long totalTime;

    public ConcurrentResult(int threads, long totalTime) {
        this.threads = threads;
        this.totalTime = totalTime;
    }

    public int getThreads() {
        return threads;
    }

    public long getTotalTime() {
        return totalTime;
    }
}
