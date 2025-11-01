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
    }
}
