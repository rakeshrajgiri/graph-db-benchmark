package org.benchmark;

import java.io.FileWriter;
import java.io.IOException;

public class CsvExporter {

    public static void export(
            BenchmarkResult result,
            int datasetSize) {

        try (FileWriter writer =
                     new FileWriter(
                             "results/arangodb-results.csv",
                             true)) {

            writer.write(
                    datasetSize
                    + ","
                    + result.getQueryName()
                    + ","
                    + result.getAverage()
                    + ","
                    + result.getMin()
                    + ","
                    + result.getMax()
                    + ","
                    + result.getP50()
                    + ","
                    + result.getP95()
                    + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportConcurrent(
            ConcurrentResult result,
            int datasetSize) {

        try (FileWriter writer =
                     new FileWriter(
                             "results/arangodb-concurrent-results.csv",
                             true)) {

            writer.write(
                    datasetSize
                    + ","
                    + result.getThreads()
                    + ","
                    + result.getTotalTime()
                    + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}