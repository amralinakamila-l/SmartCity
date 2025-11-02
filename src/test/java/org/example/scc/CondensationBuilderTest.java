package org.example.scc;

import org.example.graph.GraphData;
import org.example.graph.Edge;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CondensationBuilderTest {

    @Test
    void testCondensationBuild() {
        List<List<Integer>> sccs = Arrays.asList(
                Arrays.asList(0, 1),
                Arrays.asList(2, 3)
        );

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 4; i++) adj.add(new ArrayList<>());

        GraphData graph = new GraphData();
        graph.n = 4;
        graph.directed = true;
        graph.edges = Arrays.asList(
                new Edge(0, 1, 2),
                new Edge(1, 0, 3),
                new Edge(0, 2, 1),
                new Edge(1, 3, 4)
        );

        CondensationBuilder.Result result = CondensationBuilder.build(sccs, adj, graph);

        assertEquals(2, result.dagAdj.size());

        assertEquals(4, result.nodeToComp.length);
        assertEquals(0, result.nodeToComp[0]);
        assertEquals(0, result.nodeToComp[1]);
        assertEquals(1, result.nodeToComp[2]);
        assertEquals(1, result.nodeToComp[3]);
        assertEquals(1, result.dagAdj.get(0).size());
        assertEquals(1, result.dagAdj.get(0).get(0)[0]);
        assertEquals(1, result.dagAdj.get(0).get(0)[1]);
        assertEquals(0, result.dagAdj.get(1).size());
    }

    @Test
    void testSingleComponentCondensation() {
        List<List<Integer>> sccs = Arrays.asList(
                Arrays.asList(0, 1, 2)
        );

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());

        GraphData graph = new GraphData();
        graph.n = 3;
        graph.directed = true;
        graph.edges = Arrays.asList(
                new Edge(0, 1, 1),
                new Edge(1, 2, 2),
                new Edge(2, 0, 3)
        );

        CondensationBuilder.Result result = CondensationBuilder.build(sccs, adj, graph);

        assertEquals(1, result.dagAdj.size());
        assertTrue(result.dagAdj.get(0).isEmpty());
        assertEquals(3, result.nodeToComp.length);
        assertEquals(0, result.nodeToComp[0]);
        assertEquals(0, result.nodeToComp[1]);
        assertEquals(0, result.nodeToComp[2]);
    }

    @Test
    void testMultipleEdgesBetweenComponents() {
        List<List<Integer>> sccs = Arrays.asList(
                Arrays.asList(0),
                Arrays.asList(1),
                Arrays.asList(2)
        );

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());

        GraphData graph = new GraphData();
        graph.n = 3;
        graph.directed = true;
        graph.edges = Arrays.asList(
                new Edge(0, 1, 5),
                new Edge(0, 1, 3),
                new Edge(0, 2, 2),
                new Edge(1, 2, 4)
        );

        CondensationBuilder.Result result = CondensationBuilder.build(sccs, adj, graph);
        assertEquals(3, result.dagAdj.size());
        assertEquals(2, result.dagAdj.get(0).size());

        int weight01 = -1;
        for (int[] edge : result.dagAdj.get(0)) {
            if (edge[0] == 1) {
                weight01 = edge[1];
                break;
            }
        }
        assertEquals(3, weight01);

        assertEquals(1, result.dagAdj.get(1).size());
        assertEquals(2, result.dagAdj.get(1).get(0)[0]);
        assertEquals(4, result.dagAdj.get(1).get(0)[1]);
    }
}
