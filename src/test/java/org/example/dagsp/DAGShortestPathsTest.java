package org.example.dagsp;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathsTest {

    @Test
    void testSimpleDAGWeighted() {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new int[]{1, 2});
        adj.get(0).add(new int[]{2, 4});
        adj.get(1).add(new int[]{2, 1});

        DAGShortestPaths.Result res = DAGShortestPaths.findShortestPathsWeighted(adj, 0);

        assertEquals(0, res.dist[0]);
        assertEquals(2, res.dist[1]);
        assertEquals(3, res.dist[2]);
    }

    @Test
    void testLongestPathWeighted() {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < 4; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new int[]{1, 2});
        adj.get(0).add(new int[]{2, 1});
        adj.get(1).add(new int[]{3, 3});
        adj.get(2).add(new int[]{3, 5});

        DAGShortestPaths.Result res = DAGShortestPaths.findLongestPathWeighted(adj);

        assertEquals(6.0, res.longestLength);
        assertEquals(Arrays.asList(0, 2, 3), res.longestPath);
    }

    @Test
    void testSingleNodeGraph() {
        List<List<int[]>> adj = new ArrayList<>();
        adj.add(new ArrayList<>());
        DAGShortestPaths.Result res = DAGShortestPaths.findShortestPathsWeighted(adj, 0);

        assertEquals(0, res.dist[0]);
    }

    @Test
    void testUnreachableNodes() {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new int[]{1, 5});

        DAGShortestPaths.Result res = DAGShortestPaths.findShortestPathsWeighted(adj, 0);

        assertEquals(0, res.dist[0]);
        assertEquals(5, res.dist[1]);
        assertEquals(Double.POSITIVE_INFINITY, res.dist[2]);
    }

    @Test
    void testNegativeWeights() {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new int[]{1, -2});
        adj.get(1).add(new int[]{2, -3});

        DAGShortestPaths.Result res = DAGShortestPaths.findShortestPathsWeighted(adj, 0);

        assertEquals(0, res.dist[0]);
        assertEquals(-2, res.dist[1]);
        assertEquals(-5, res.dist[2]);
    }
}





