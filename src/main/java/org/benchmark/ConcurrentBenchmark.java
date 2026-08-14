package org.benchmark;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.benchmark.database.GraphDbClient;

public class ConcurrentBenchmark {

    private GraphDbClient client;

    public ConcurrentBenchmark(GraphDbClient client) {
        this.client = client;
    }

    public ConcurrentResult runConcurrentTest(int threadCount) {

        System.out.println(
                "\nRunning Concurrent Test : "
                        + threadCount
                        + " Threads");

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        long start = System.currentTimeMillis();

        for(int i = 0; i < threadCount; i++) {

            executor.submit(() -> {

                client.runBenchmark(
                        "ConcurrentLookup",
                        """
                        MATCH (p:Person {id:50})
                        RETURN p
                        """);
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(
                    10,
                    TimeUnit.MINUTES);

        } catch(Exception e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        long totalTime = end - start;

        System.out.println(
                "Completed in "
                        + totalTime
                        + " ms");

        return new ConcurrentResult(
                threadCount,
                totalTime);
    }
}