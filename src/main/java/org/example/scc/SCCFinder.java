package org.example.scc;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
        disc = new int[n];
        low = new int[n];
        stackMember = new boolean[n];
        stack = new Stack<>();

        for (int i = 0; i < n; i++) {
            disc[i] = -1;
            low[i] = -1;
        }

        List<List<Integer>> sccs = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i, sccs);
            }
        }

        return sccs;
    }

    private void dfs(int u, List<List<Integer>> sccs) {
        disc[u] = low[u] = ++time;
        stack.push(u);
        stackMember[u] = true;

        for (int v : adj.get(u)) {
            if (disc[v] == -1) {
                dfs(v, sccs);
                low[u] = Math.min(low[u], low[v]);
            } else if (stackMember[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

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

