package org.example.dagsp;

import org.example.metrics.Metrics;
import java.util.*;

/**
 * Ожидает взвешенный DAG в формате List<List<int[]>> где int[]{to, weight}
 */
public class DAGShortestPaths {

    public static class Result {
        public final int[] dist;
        public final int[] prev;
        public Result(int[] dist, int[] prev) { this.dist = dist; this.prev = prev; }
    }

    /**
     * Shortest paths from source (weights >= possibly negative but DAG => ok).
     * @return dist[] and prev[] (prev[source] = -1)
     */
    public static Result shortestPaths(List<List<int[]>> dag, int source, List<Integer> topo, Metrics metrics) {
        int n = dag.size();
        final int INF = Integer.MAX_VALUE / 4;
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        metrics.inc("sp_start");

        // process in topo order
        for (int u : topo) {
            if (dist[u] == INF) continue;
            for (int[] e : dag.get(u)) {
                int v = e[0], w = e[1];
                metrics.inc("relaxations");
                long cand = (long)dist[u] + w;
                if (cand < dist[v]) {
                    dist[v] = (int)cand;
                    prev[v] = u;
                }
            }
        }
        metrics.inc("sp_done");
        return new Result(dist, prev);
    }

    /**
     * Longest path (critical path) from source: max-DP over topo.
     * @return pair (distMax[], prevMax[])
     */
    public static Result longestPaths(List<List<int[]>> dag, int source, List<Integer> topo, Metrics metrics) {
        int n = dag.size();
        final int NEGINF = Integer.MIN_VALUE / 4;
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, NEGINF);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        for (int u : topo) {
            if (dist[u] == NEGINF) continue;
            for (int[] e : dag.get(u)) {
                int v = e[0], w = e[1];
                metrics.inc("relaxations");
                int cand = dist[u] + w;
                if (cand > dist[v]) {
                    dist[v] = cand;
                    prev[v] = u;
                }
            }
        }
        return new Result(dist, prev);
    }

    /** Reconstruct path from source -> target via prev[] */
    public static List<Integer> reconstructPath(int[] prev, int target) {
        List<Integer> path = new ArrayList<>();
        int cur = target;
        while (cur != -1) {
            path.add(cur);
            cur = prev[cur];
        }
        Collections.reverse(path);
        return path;
    }
}


