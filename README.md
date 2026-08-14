# Graph Database Benchmark

## Overview

This project benchmarks graph database performance using the CA-HepPh collaboration network dataset.

The benchmark evaluates:

- Dataset loading performance
- 1-Hop Traversal
- 2-Hop Traversal
- 3-Hop Traversal
- Point Lookup
- Aggregation Queries
- Concurrent Query Execution

## Databases Benchmarked

1. CognoDB Cloud
2. Neo4j AuraDB Free
3. Memgraph Cloud

## Databases Investigated

- ArangoDB Cloud (protocol incompatibility with Neo4j Bolt benchmark implementation)
- FalkorDB Cloud (connection timeout during benchmark execution)

---

## Dataset

Dataset: CA-HepPh Collaboration Network

Source:

https://snap.stanford.edu/data/ca-HepPh.html

Dataset Size Tested:

- 100,000 Relationships
- 200,000 Relationships

---

## Benchmark Queries

### 1-Hop Traversal

```cypher
MATCH (p:Person {id:7279})-[:FRIEND]->(f)
RETURN count(f)
```

### 2-Hop Traversal

```cypher
MATCH (p:Person {id:7279})
-[:FRIEND]->()
-[:FRIEND]->(f)
RETURN count(f)
```

### 3-Hop Traversal

```cypher
MATCH (p:Person {id:7279})
-[:FRIEND]->()
-[:FRIEND]->()
-[:FRIEND]->(f)
RETURN count(f)
```

### Point Lookup

```cypher
MATCH (p:Person {id:7279})
RETURN p
```

### Aggregation

```cypher
MATCH (p:Person)
RETURN count(p)
```

---

## CognoDB Results (200k Relationships)

### Query Performance

| Query | Avg (ms) | P50 (ms) | P95 (ms) |
|---------|---------|---------|---------|
| 1-Hop | 604.90 | 629 | 1145 |
| 2-Hop | 725.10 | 629 | 1435 |
| 3-Hop | 618.25 | 596 | 1376 |
| Point Lookup | 733.50 | 637 | 1527 |
| Aggregation | 618.65 | 623 | 840 |

### Concurrent Performance

| Threads | Total Time (ms) |
|----------|----------------|
| 10 | 13547 |
| 20 | 9589 |
| 50 | 10487 |

---

## Neo4j AuraDB Results (200k Relationships)

### Query Performance

| Query | Avg (ms) | P50 (ms) | P95 (ms) |
|---------|---------|---------|---------|
| 1-Hop | 262.65 | 176 | 773 |
| 2-Hop | 202.70 | 166 | 758 |
| 3-Hop | 198.15 | 161 | 486 |
| Point Lookup | 238.50 | 216 | 546 |
| Aggregation | 221.85 | 179 | 583 |

### Concurrent Performance

| Threads | Total Time (ms) |
|----------|----------------|
| 10 | 4015 |
| 20 | 3454 |
| 50 | 5371 |

---

## Memgraph Results (200k Relationships)

### Query Performance

| Query | Avg (ms) | P50 (ms) | P95 (ms) |
|---------|---------|---------|---------|
| 1-Hop | 795.60 | 692 | 2051 |
| 2-Hop | 704.85 | 691 | 861 |
| 3-Hop | 667.15 | 664 | 691 |
| Point Lookup | 791.25 | 663 | 1672 |
| Aggregation | 730.55 | 682 | 976 |

### Concurrent Performance

| Threads | Total Time (ms) |
|----------|----------------|
| 10 | 17280 |
| 20 | 14169 |
| 50 | 11870 |

---

## Methodology

- Same benchmark code was used for all databases.
- Same CA-HepPh dataset was loaded into each database.
- Same query workloads were executed across all platforms.
- Measurements include:
  - Average Latency
  - Minimum Latency
  - Maximum Latency
  - P50 Latency
  - P95 Latency
  - Concurrent Execution Time
- Dataset loading performance was recorded separately.

---

## Observations

- Neo4j AuraDB produced the lowest query latency across most workloads.
- CognoDB showed competitive graph traversal performance.
- Memgraph completed all workloads successfully but exhibited higher latency.
- Concurrent workload performance varied between platforms.
- Performance differences may be influenced by cloud infrastructure, free-tier limitations, and network latency.

---

## Project Structure

```text
graph-db-benchmark
│
├── src
│   ├── main
│   └── test
│
├── results
│
├── CA-HepPh.txt
├── pom.xml
└── README.md
```

## Running the Benchmark

Set environment variables:

```bash
NEO4J_URI=<database-uri>
NEO4J_USERNAME=<username>
NEO4J_PASSWORD=<password>
```

Compile:

```bash
mvn clean compile
```

Run:

```bash
mvn exec:java "-Dexec.mainClass=org.benchmark.BenchmarkRunner"
```

## Author

Rakesh Rajgiri