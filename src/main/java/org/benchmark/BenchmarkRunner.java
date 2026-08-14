package org.benchmark;

import org.benchmark.database.GraphDbClient;

public class BenchmarkRunner {

    public static void main(String[] args) {


        String uri = System.getenv("NEO4J_URI");

        String username = System.getenv("NEO4J_USERNAME");

        String password = System.getenv("NEO4J_PASSWORD");
        

        GraphDbClient client =new GraphDbClient(uri,username,password);



       /* DataGenerator generator = new DataGenerator(client.getDriver());
        
        generator.clearDatabase();
        generator.generateUsers();
        generator.generateFriendships();

*/      
        try(var session = client.getDriver().session()) {
                session.run("""
                               MATCH (n)
                               DETACH DELETE n
                            """);

                System.out.println("Database Cleared");
        } 
        int datasetSize = 200000;
        DatasetLoader loader =
        new DatasetLoader(client.getDriver());
        loader.loadDataset("ca-HepPh.txt", datasetSize);

        BenchmarkResult oneHop =
                client.runBenchmark(
                        "1-Hop",
                        """
                        MATCH (p:Person {id:7279})-[:FRIEND]->(f)
                        RETURN count(f)
                        """);

        BenchmarkResult twoHop =
                client.runBenchmark(
                        "2-Hop",
                        """
                        MATCH (p:Person {id:7279})
                        -[:FRIEND]->()
                        -[:FRIEND]->(f)
                        RETURN count(f)
                        """);

        BenchmarkResult threeHop =
                client.runBenchmark(
                        "3-Hop","""
                        MATCH (p:Person {id:7279})
                        -[:FRIEND]->()
                        -[:FRIEND]->()
                        -[:FRIEND]->(f)
                        RETURN count(f)
                        """);

        BenchmarkResult lookup =
        client.runBenchmark(
                "Point-Lookup",
                """
                MATCH (p:Person {id:7279})
                RETURN p
                """);
        BenchmarkResult aggregation = client.runBenchmark("Aggregation","""
                    MATCH (p:Person)
                    RETURN count(p)
                        """);

        

        // Concurrent Testing
        ConcurrentBenchmark concurrent = new ConcurrentBenchmark(client);

        ConcurrentResult t10 = concurrent.runConcurrentTest(10);

        ConcurrentResult t20 = concurrent.runConcurrentTest(20);

        ConcurrentResult t50 = concurrent.runConcurrentTest(50);

        CsvExporter.exportConcurrent(t10, datasetSize);
        CsvExporter.exportConcurrent(t20, datasetSize);
        CsvExporter.exportConcurrent(t50, datasetSize);

        System.out.println(oneHop.getQueryName()
        + " Avg="
        + oneHop.getAverage()
        + " P50="
        + oneHop.getP50()
        + " P95="
        + oneHop.getP95());

        System.out.println(twoHop.getQueryName()
        + " Avg="
        + twoHop.getAverage()
        + " P50="
        + twoHop.getP50()
        + " P95="
        + twoHop.getP95());

        System.out.println(threeHop.getQueryName()
        + " Avg="
        + threeHop.getAverage()
        + " P50="
        + threeHop.getP50()
        + " P95="
        + threeHop.getP95());

        System.out.println(lookup.getQueryName()
        + " Avg="
        + lookup.getAverage()
        + " P50="
        + lookup.getP50()
        + " P95="
        + lookup.getP95());

        System.out.println(aggregation.getQueryName()
        + " Avg="
        + aggregation.getAverage()
        + " P50="
        + aggregation.getP50()
        + " P95="
        + aggregation.getP95());

        CsvExporter.export(oneHop, datasetSize);
        CsvExporter.export(twoHop, datasetSize);
        CsvExporter.export(threeHop, datasetSize);
        CsvExporter.export(lookup, datasetSize);
        CsvExporter.export(aggregation, datasetSize);

        client.close();
    }
}
