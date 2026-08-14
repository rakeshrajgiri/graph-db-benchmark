package org.benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;




public class DatasetLoader {

    private Driver driver;
    

    public DatasetLoader(Driver driver) {
        this.driver = driver;
    }

    public void loadDataset(String filePath,int maxRelationships) {

    System.out.println("Loading dataset...");

    final int BATCH_SIZE = 5000;

    try (
            BufferedReader br =
                    new BufferedReader(
                            new FileReader(filePath));

            Session session = driver.session()) {

                session.run("""
                        CREATE INDEX person_id_index IF NOT EXISTS
                        FOR (p:Person)
                        ON (p.id)
                            """).consume();

        String line;

        int totalCount = 0;

        List<Map<String,Object>> batch = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        while ((line = br.readLine()) != null) {

            if (line.startsWith("#")) {
                continue;
            }

            String[] parts =
                    line.trim().split("\\s+");

            if (parts.length < 2) {
                continue;
            }

            int from =
                    Integer.parseInt(parts[0]);

            int to =
                    Integer.parseInt(parts[1]);

            Map<String,Object> row =
                    new HashMap<>();

            row.put("from", from);
            row.put("to", to);

            batch.add(row);

            if (batch.size() == BATCH_SIZE) {

                session.run(
                        """
                        UNWIND $rows AS row

                        MERGE (a:Person {id: row.from})
                        MERGE (b:Person {id: row.to})

                        MERGE (a)-[:FRIEND]->(b)
                        """,
                        Values.parameters(
                                "rows",
                                batch))
                        .consume();

                totalCount += batch.size();

                System.out.println(
                        totalCount
                        + " relationships loaded");

                batch.clear();

                // Stop at 100k relationships
                if(totalCount >= maxRelationships) {
                    break;
                }
            }
        }
        System.out.println(
        "Dataset Loaded Successfully");

System.out.println(
        "Relationships = " + totalCount);

long endTime = System.currentTimeMillis();

double seconds =
        (endTime - startTime) / 1000.0;

System.out.println(
        "Load Time = " + seconds + " sec");

System.out.println(
        "Relationships/sec = "
                + (totalCount / seconds));
        

        // Remaining records
        if(!batch.isEmpty()) {

            session.run(
                    """
                    UNWIND $rows AS row

                    MERGE (a:Person {id: row.from})
                    MERGE (b:Person {id: row.to})

                    MERGE (a)-[:FRIEND]->(b)
                    """,
                    Values.parameters(
                            "rows",
                            batch))
                    .consume();

            totalCount += batch.size();
        }

        System.out.println(
                "Dataset Loaded Successfully");

        System.out.println(
                "Relationships = "
                + totalCount);

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
        