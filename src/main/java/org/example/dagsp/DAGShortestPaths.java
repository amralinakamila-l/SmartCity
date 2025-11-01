package org.example.dagsp;

import org.example.metrics.Metrics;

import java.util.*;

/**
 * Алгоритмы кратчайших и длиннейших путей в DAG.
 * Работает по топологическому порядку.
 */
public class DAGShortestPaths {

    public static class Result {
        public double[] dist;
        public int[] parent;
        public int source;
        public double longestLength;
        public List<Integer> longestPath;

        public Result(double[] dist, int[] parent, int source) {
            this.dist = dist;
            this.parent = parent;
            this.source = source;
        }
    }

    /** Найти кратчайшие пути из одного источника в DAG */
    public static Result findShortestPaths(List<List<Integer>> dag, int source) {
        int n = dag.size();
        double[] dist = new double[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        Metrics metrics = new Metrics();
        metrics.start();

        // Получим топологический порядок
        List<Integer> topo = topoSort(dag);

        // Динамическое обновление
        for (int u : topo) {
            for (int v : dag.get(u)) {
                metrics.increment("relaxations");
                if (dist[u] + 1 < dist[v]) {
                    dist[v] = dist[u] + 1;
                    parent[v] = u;
                }
            }
        }

        metrics.stop();
        metrics.print("DAG Shortest Paths");

        return new Result(dist, parent, source);
    }

    /** Найти самый длинный путь (critical path) в DAG */
    public static Result findLongestPath(List<List<Integer>> dag) {
        int n = dag.size();
        double[] dist = new double[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Double.NEGATIVE_INFINITY);
        Arrays.fill(parent, -1);

        Metrics metrics = new Metrics();
        metrics.start();

        List<Integer> topo = topoSort(dag);
        dist[0] = 0;

        for (int u : topo) {
            for (int v : dag.get(u)) {
                metrics.increment("relaxations");
                if (dist[u] + 1 > dist[v]) {
                    dist[v] = dist[u] + 1;
                    parent[v] = u;
                }
            }
        }

        // Найдём конец критического пути
        double maxDist = Double.NEGATIVE_INFINITY;
        int end = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] > maxDist) {
                maxDist = dist[i];
                end = i;
            }
        }

        // Восстановим путь
        List<Integer> path = new ArrayList<>();
        while (end != -1) {
            path.add(end);
            end = parent[end];
        }
        Collections.reverse(path);

        metrics.stop();
        metrics.print("DAG Longest Path");

        Result r = new Result(dist, parent, 0);
        r.longestLength = maxDist;
        r.longestPath = path;
        return r;
    }

    // Вспомогательный метод: топологическая сортировка
    private static List<Integer> topoSort(List<List<Integer>> dag) {
        int n = dag.size();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++)
            for (int v : dag.get(u)) indeg[v]++;

        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++)
            if (indeg[i] == 0) q.add(i);

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (int v : dag.get(u)) {
                indeg[v]--;
                if (indeg[v] == 0) q.add(v);
            }
        }

        return order;
    }
}


