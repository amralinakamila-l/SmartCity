package org.example.dagsp;

import org.example.metrics.Metrics;

import java.util.*;

public class DAGShortestPaths {

    public static class Result {
        public double[] dist;
        public int[] parent;
        public int source;
        public double longestLength;
        public List<Integer> longestPath;
        public Metrics metrics;

        public Result(double[] dist, int[] parent, int source, Metrics metrics) {
            this.dist = dist;
            this.parent = parent;
            this.source = source;
            this.metrics = metrics;
        }
    }

    // ==========================
    // Взвешенный DAG
    // ==========================
    public static Result findShortestPathsWeighted(List<List<int[]>> wadj, int source) {
        Metrics metrics = new Metrics();
        metrics.start();

        int n = wadj.size();
        double[] dist = new double[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        List<Integer> topo = topoSortWeighted(wadj);

        for (int u : topo) {
            if (dist[u] == Double.POSITIVE_INFINITY) continue;

            for (int[] e : wadj.get(u)) {
                metrics.increment("relaxations");
                int v = e[0];
                int w = e[1];
                double newDist = dist[u] + w;
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    parent[v] = u;
                }
            }
        }

        metrics.stop();
        return new Result(dist, parent, source, metrics);
    }

    public static Result findLongestPathWeighted(List<List<int[]>> wadj) {
        Metrics metrics = new Metrics();
        metrics.start();

        int n = wadj.size();
        double[] dist = new double[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Double.NEGATIVE_INFINITY);
        Arrays.fill(parent, -1);

        List<Integer> topo = topoSortWeighted(wadj);

        // Находим и инициализируем все источники (вершины с входящей степенью 0)
        boolean[] hasIncoming = new boolean[n];
        for (int u = 0; u < n; u++) {
            for (int[] e : wadj.get(u)) {
                hasIncoming[e[0]] = true;
            }
        }

        for (int i = 0; i < n; i++) {
            if (!hasIncoming[i]) {
                dist[i] = 0;
            }
        }

        for (int u : topo) {
            if (dist[u] == Double.NEGATIVE_INFINITY) continue;

            for (int[] e : wadj.get(u)) {
                metrics.increment("relaxations");
                int v = e[0];
                int w = e[1];
                double newDist = dist[u] + w;
                if (newDist > dist[v]) {
                    dist[v] = newDist;
                    parent[v] = u;
                }
            }
        }

        // Находим максимальный путь
        double maxDist = Double.NEGATIVE_INFINITY;
        int end = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] > maxDist && dist[i] != Double.NEGATIVE_INFINITY) {
                maxDist = dist[i];
                end = i;
            }
        }

        List<Integer> path = new ArrayList<>();
        if (end != -1) {
            // Восстанавливаем путь от конца к началу
            int current = end;
            while (current != -1) {
                path.add(current);
                current = parent[current];
            }
            Collections.reverse(path);
        }

        metrics.stop();

        Result r = new Result(dist, parent, 0, metrics);
        r.longestLength = maxDist;
        r.longestPath = path;
        return r;
    }

    // ==========================
    // Вспомогательные топологические сортировки
    // ==========================
    private static List<Integer> topoSortWeighted(List<List<int[]>> wadj) {
        int n = wadj.size();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            for (int[] e : wadj.get(u)) {
                indeg[e[0]]++;
            }
        }

        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) q.add(i);
        }

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (int[] e : wadj.get(u)) {
                int v = e[0];
                indeg[v]--;
                if (indeg[v] == 0) q.add(v);
            }
        }
        return order;
    }
}



