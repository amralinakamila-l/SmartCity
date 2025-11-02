package org.example.graph.utils;

import org.example.graph.Edge;
import org.example.graph.GraphData;

import java.util.ArrayList;
import java.util.List;

public class GraphBuilder {

    public static List<List<Integer>> buildAdjList(GraphData graph) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < graph.n; i++) {
            adj.add(new ArrayList<>());
        }

        for (Edge e : graph.edges) {
            adj.get(e.u).add(e.v);
            if (!graph.directed && e.u != e.v) {
                adj.get(e.v).add(e.u);
            }
        }

        return adj;
    }
}
