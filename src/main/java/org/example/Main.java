package org.example;

import com.google.gson.Gson;
import org.example.graph.GraphData;
import org.example.graph.utils.GraphBuilder;
import org.example.scc.SCCFinder;
import org.example.topo.TopologicalSort;
import org.example.dagsp.DAGShortestPaths;

import java.io.FileReader;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Gson gson = new Gson();
        GraphData graph = gson.fromJson(new FileReader("tasks.json"), GraphData.class);

        var adj = GraphBuilder.buildAdjList(graph);

        System.out.println("Original graph adjacency list:");
        for (int i = 0; i < adj.size(); i++) {
            System.out.println(i + " -> " + adj.get(i));
        }

        SCCFinder sccFinder = new SCCFinder(adj);
        List<List<Integer>> sccs = sccFinder.findSCCs();

        System.out.println("\nStrongly Connected Components:");
        for (int i = 0; i < sccs.size(); i++) {
            System.out.println("SCC " + i + ": " + sccs.get(i));
        }

        // Построим DAG между компонентами
        Map<Integer, Integer> nodeToSCC = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (int v : sccs.get(i)) {
                nodeToSCC.put(v, i);
            }
        }

        List<List<Integer>> dag = new ArrayList<>();
        for (int i = 0; i < sccs.size(); i++) dag.add(new ArrayList<>());

        for (int u = 0; u < adj.size(); u++) {
            for (int v : adj.get(u)) {
                int su = nodeToSCC.get(u);
                int sv = nodeToSCC.get(v);
                if (su != sv && !dag.get(su).contains(sv)) {
                    dag.get(su).add(sv);
                }
            }
        }

        System.out.println("\nCondensed DAG adjacency list:");
        for (int i = 0; i < dag.size(); i++) {
            System.out.println(i + " -> " + dag.get(i));
        }

        // Топологическая сортировка DAG
        List<Integer> topo = TopologicalSort.sort(dag);
        System.out.println("\nTopological order: " + topo);

        // Пример для поиска кратчайших путей (если DAG имеет веса)
        // Тут можно добавить веса между SCC вручную
    }
}
