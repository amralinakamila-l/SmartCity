package org.example.topo;

import org.example.metrics.Metrics;
import java.util.*;

/**
 * Топологическая сортировка (алгоритм Кана) с метриками.
 */
public class TopologicalSort {

    /**
     * Топологическая сортировка для обычного DAG (без весов).
     * Считает количество pops/pushes и время выполнения.
     */
    public static List<Integer> sort(List<List<Integer>> adj) {
        Metrics metrics = new Metrics();
        metrics.start();

        int n = adj.size();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) indeg[v]++;
        }

        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) q.add(i);
        }

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            metrics.increment("queue_pops");
            order.add(u);
            for (int v : adj.get(u)) {
                indeg[v]--;
                metrics.increment("edges_processed");
                if (indeg[v] == 0) {
                    q.add(v);
                    metrics.increment("queue_pushes");
                }
            }
        }

        metrics.stop();
        metrics.print("Kahn TopoSort");

        if (order.size() != n) {
            throw new IllegalStateException("Graph is not a DAG (contains cycle)");
        }

        return order;
    }

    /**
     * Вариант для взвешенного DAG (adjacency list: int[]{to, weight})
     */
    public static List<Integer> sortWeighted(List<List<int[]>> wadj) {
        Metrics metrics = new Metrics();
        metrics.start();

        int n = wadj.size();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            for (int[] e : wadj.get(u)) indeg[e[0]]++;
        }

        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) q.add(i);

        List<Integer> out = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            metrics.increment("queue_pops");
            out.add(u);
            for (int[] e : wadj.get(u)) {
                int v = e[0];
                indeg[v]--;
                metrics.increment("edges_processed");
                if (indeg[v] == 0) {
                    q.add(v);
                    metrics.increment("queue_pushes");
                }
            }
        }

        metrics.stop();
        metrics.print("Kahn TopoSort (Weighted)");

        if (out.size() != n) {
            throw new IllegalStateException("Graph is not a DAG (contains cycle)");
        }

        return out;
    }
}

