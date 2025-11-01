package org.example.scc;

import org.example.graph.Edge;
import org.example.graph.GraphData;

import java.util.*;

/**
 * Строит конденсационный граф (компонента -> компонента) и
 * сохраняет веса рёбер как минимальный вес между компонентами.
 */
public class CondensationBuilder {

    public static class Result {
        public final List<List<int[]>> dagAdj;
        public final int[] nodeToComp;
        public Result(List<List<int[]>> dagAdj, int[] nodeToComp) {
            this.dagAdj = dagAdj; this.nodeToComp = nodeToComp;
        }
    }

    public static Result build(List<List<Integer>> sccs, List<List<Integer>> adj, GraphData graph) {
        int comps = sccs.size();
        int n = adj.size();

        // node -> comp id
        int[] nodeToComp = new int[n];
        for (int i = 0; i < comps; i++) {
            for (int v : sccs.get(i)) nodeToComp[v] = i;
        }

        // use map pair (uComp, vComp) -> minWeight
        Map<Long, Integer> minEdge = new HashMap<>();
        for (Edge e : graph.edges) {
            int cu = nodeToComp[e.u], cv = nodeToComp[e.v];
            if (cu == cv) continue;
            long key = ((long)cu << 32) | (cv & 0xffffffffL);
            int w = e.w;
            minEdge.merge(key, w, Math::min);
        }

        List<List<int[]>> dag = new ArrayList<>();
        for (int i = 0; i < comps; i++) dag.add(new ArrayList<>());

        for (var ent : minEdge.entrySet()) {
            long key = ent.getKey();
            int cu = (int)(key >> 32);
            int cv = (int)key;
            int w = ent.getValue();
            dag.get(cu).add(new int[]{cv, w});
        }

        return new Result(dag, nodeToComp);
    }
}

