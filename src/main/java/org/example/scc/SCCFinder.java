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

    private final Metrics metrics = new Metrics();

    public SCCFinder(List<List<Integer>> adj) {
        this.adj = adj;
        this.n = adj.size();
    }

    public List<List<Integer>> findSCCs() {
        metrics.start(); // ⏱ запускаем таймер

        disc = new int[n];
        low = new int[n];
        stackMember = new boolean[n];
        stack = new Stack<>();

        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);

        List<List<Integer>> sccs = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i, sccs);
            }
        }

        metrics.stop(); // ⏹ останавливаем
        metrics.print("Tarjan SCC"); // 📊 выводим статистику

        return sccs;
    }

    private void dfs(int u, List<List<Integer>> sccs) {
        metrics.increment("dfs_visits");

        disc[u] = low[u] = ++time;
        stack.push(u);
        stackMember[u] = true;

        for (int v : adj.get(u)) {
            metrics.increment("edges_checked");

            if (disc[v] == -1) {
                dfs(v, sccs);
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


