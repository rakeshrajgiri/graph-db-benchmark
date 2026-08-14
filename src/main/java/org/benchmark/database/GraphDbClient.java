package org.benchmark.database;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import org.benchmark.BenchmarkResult;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

public class GraphDbClient {

	private Driver driver;

	public GraphDbClient(String uri,String username,String password) {
		driver=GraphDatabase.driver(uri,AuthTokens.basic(username,password));
	}

	public void testConnection() {

		try(Session session = driver.session()) {

			String result=session.run("RETURN 'Connected' AS msg")
							.single()
							.get("msg")
							.asString();

			System.out.println(result);
		}
	}
	public void createSampleData() {

		try(Session session = driver.session()) {

		    session.run("""
			    CREATE (r:Rakesh {name:'Rakesh'})
			    CREATE (a:Person {name:'Anil'})
			    CREATE (v:Person {name:'Vijay'})
			    CREATE (r)-[:FRIEND]->(a)
			    CREATE (a)-[:FRIEND]->(v)
		    """);

		    System.out.println("Sample Data Created");
		}
	}
	public void runQuery() {
 
		try(Session session = driver.session()) {

			Result result = session.run("""
			MATCH (n)-[:FRIEND]->(m)
			RETURN n.name,m.name""");

			while(result.hasNext()) {
				Record record = result.next();
				System.out.println(record.get("n.name").asString()
				+ " -> "
				+ record.get("m.name").asString()
				);
			}
		}
	}

	public BenchmarkResult runBenchmark(String queryName,String query) {

	    List<Long> latencies = new ArrayList<>();
        try(Session session = driver.session()) {
        
	    for(int i = 0; i <20; i++) {

		    long start = System.nanoTime();

                session.run(query).consume();
		    

		    long end = System.nanoTime();

		    long latency = (end - start) / 1_000_000;

		    latencies.add(latency);
	    }
	    }
		if(latencies.isEmpty()) {
        throw new RuntimeException("No benchmark data collected");
        }
	    Collections.sort(latencies);
        long sum = 0;

	    for(Long latency : latencies) {
		    sum += latency;
	    }

	    double average = (double) sum/latencies.size();

	    long min = latencies.get(0);

	    long max = latencies.get(latencies.size() - 1);
 
    	long p50 = latencies.get(latencies.size() / 2);

	    long p95 = latencies.get((int)(latencies.size() * 0.95));

	    return new BenchmarkResult(
            queryName,
            average,
            min,
            max,
            p50,
            p95);
	}
		 
        public Driver getDriver() {
        return driver;
        }
	

    public void close() {
		driver.close();
	}


}
