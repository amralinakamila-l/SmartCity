package org.example.graph.utils;

import org.example.graph.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class GraphBuilderTest {

    @Test
    void testBuildAdjList() {
        GraphData g = new GraphData();
        g.n = 3;
        g.directed = true;
        g.edges = Arrays.asList(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1)
        );

        var adj = GraphBuilder.buildAdjList(g);

        assertEquals(Arrays.asList(1), adj.get(0));
        assertEquals(Arrays.asList(2), adj.get(1));
        assertEquals(Arrays.asList(), adj.get(2));
    }

    @Test
    void testBuildAdjListUndirected() {
        GraphData g = new GraphData();
        g.n = 3;
        g.directed = false;
        g.edges = Arrays.asList(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1)
        );
        var adj = GraphBuilder.buildAdjList(g);

        assertEquals(Arrays.asList(1), adj.get(0));
        assertEquals(Arrays.asList(0, 2), adj.get(1));
        assertEquals(Arrays.asList(1), adj.get(2));

        assertTrue(adj.get(1).contains(0));
        assertTrue(adj.get(1).contains(2));
        assertEquals(2, adj.get(1).size());
    }

    @Test
    void testEmptyGraph() {
        GraphData g = new GraphData();
        g.n = 3;
        g.directed = true;
        g.edges = Collections.emptyList();

        var adj = GraphBuilder.buildAdjList(g);

        assertEquals(3, adj.size());
        for (int i = 0; i < 3; i++) {
            assertTrue(adj.get(i).isEmpty());
        }
    }

    @Test
    void testSelfLoopDirected() {
        GraphData g = new GraphData();
        g.n = 2;
        g.directed = true;
        g.edges = Arrays.asList(
                new Edge(0, 0, 1),  // self-loop
                new Edge(0, 1, 2)
        );

        var adj = GraphBuilder.buildAdjList(g);
        assertEquals(Arrays.asList(0, 1), adj.get(0));
        assertEquals(Arrays.asList(), adj.get(1));
    }

    @Test
    void testSelfLoopUndirected() {
        GraphData g = new GraphData();
        g.n = 2;
        g.directed = false;
        g.edges = Arrays.asList(
                new Edge(0, 0, 1),
                new Edge(0, 1, 2)
        );

        var adj = GraphBuilder.buildAdjList(g);

        assertEquals(2, adj.get(0).size());
        assertTrue(adj.get(0).contains(0));
        assertTrue(adj.get(0).contains(1));

        List<Integer> node0Neighbors = adj.get(0);
        Collections.sort(node0Neighbors);
        assertEquals(Arrays.asList(0, 1), node0Neighbors);

        assertEquals(Arrays.asList(0), adj.get(1));
    }
}
