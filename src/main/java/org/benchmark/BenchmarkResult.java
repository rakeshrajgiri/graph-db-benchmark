package org.benchmark;

public class BenchmarkResult {

    private String queryName;
    private double average;
    private long min;
    private long max;
    private long p50;
    private long p95;

    public BenchmarkResult(
            String queryName,
            double average,
            long min,
            long max,
            long p50,
            long p95) {

        this.queryName = queryName;
        this.average = average;
        this.min = min;
        this.max = max;
        this.p50 = p50;
        this.p95 = p95;
    }

    public String getQueryName() {
        return queryName;
    }

    public double getAverage() {
        return average;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public long getP50() {
        return p50;
    }

    public long getP95() {
        return p95;
    }
}
