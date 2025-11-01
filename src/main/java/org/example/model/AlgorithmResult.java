package org.example.model;

import java.util.List;
import org.example.graph.Edge;

public class AlgorithmResult {
    public String algorithm;
    public int nodes;
    public int edges;
    public double totalWeight;
    public double executionTimeMs;
    public int operations;
    public List<Edge> edgesInMst;

    public AlgorithmResult(String algorithm, int nodes, int edges, double totalWeight,
                           double executionTimeMs, int operations, List<Edge> edgesInMst) {
        this.algorithm = algorithm;
        this.nodes = nodes;
        this.edges = edges;
        this.totalWeight = totalWeight;
        this.executionTimeMs = executionTimeMs;
        this.operations = operations;
        this.edgesInMst = edgesInMst;
    }
}

