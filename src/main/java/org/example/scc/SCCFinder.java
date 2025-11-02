package org.example.scc;

import org.example.metrics.Metrics;
import java.util.*;

public class SCCFinder {
    private final List<List<Integer>> adj;
    private final int n;

    private int time = 0;
    private int[] disc;
    private int[] low;
    private boolean[] stackMember;
    private Stack<Integer> stack;

    public SCCFinder(List<List<Integer>> adj) {
        this.adj = adj;
        this.n = adj.size();
    }

    public List<List<Integer>> findSCCs() {
        Metrics metrics = new Metrics();
        metrics.start();

        disc = new int[n];
        low = new int[n];
        stackMember = new boolean[n];
        stack = new Stack<>();

        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);

        List<List<Integer>> sccs = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i, sccs, metrics);
            }
        }

        metrics.stop();
        metrics.print("Tarjan SCC");

        System.out.println("Found " + sccs.size() + " SCCs:");
        for (int i = 0; i < sccs.size(); i++) {
            System.out.println("  SCC " + i + ": " + sccs.get(i).size() + " nodes - " + sccs.get(i));
        }

        return sccs;
    }

    private void dfs(int u, List<List<Integer>> sccs, Metrics metrics) {
        metrics.increment("dfs_visits");

        disc[u] = low[u] = ++time;
        stack.push(u);
        stackMember[u] = true;

        for (int v : adj.get(u)) {
            metrics.increment("edges_checked");

            if (disc[v] == -1) {
                dfs(v, sccs, metrics);
                low[u] = Math.min(low[u], low[v]);
            } else if (stackMember[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // Если нашли корень SCC
        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                stackMember[w] = false;
                component.add(w);
            } while (w != u);
            sccs.add(component);
        }
    }
}


