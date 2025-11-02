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
            // ТОЛЬКО направленные ребра
            adj.get(e.u).add(e.v);
            // Убрали добавление обратных ребер для неориентированных графов
        }

        return adj;
    }
}
