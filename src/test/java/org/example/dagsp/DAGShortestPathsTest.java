package org.example.dagsp;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathsTest {

    @Test
    void testSimpleDAG() {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new int[]{1, 2});
        adj.get(0).add(new int[]{2, 4});
        adj.get(1).add(new int[]{2, 1});

        List<Integer> topo = Arrays.asList(0, 1, 2);
        int[] dist = DAGShortestPaths.findShortestPaths(adj, 0, topo);

        assertEquals(0, dist[0]);
        assertEquals(2, dist[1]);
        assertEquals(3, dist[2]);
    }
}





