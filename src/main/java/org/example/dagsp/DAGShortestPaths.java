package org.example.dagsp;

import java.util.*;

public class DAGShortestPaths {
    public static int[] findShortestPaths(List<List<int[]>> adj, int source, List<Integer> topoOrder) {
        int n = adj.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (int[] edge : adj.get(u)) {
                    int v = edge[0], w = edge[1];
                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                    }
                }
            }
        }
        return dist;
    }
}

