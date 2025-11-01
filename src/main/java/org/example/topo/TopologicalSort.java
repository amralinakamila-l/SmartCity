package org.example.topo;

import java.util.*;

public class TopologicalSort {
    public static List<Integer> sort(List<List<Integer>> adj) {
        int n = adj.size();
        int[] indegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) {
                indegree[v]++;
            }
        }

        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) q.add(i);
        }

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (int v : adj.get(u)) {
                indegree[v]--;
                if (indegree[v] == 0) q.add(v);
            }
        }
        return order;
    }
}


