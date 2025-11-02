package org.example.topo;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TopologicalSortTest {

    @Test
    void testTopoOrder() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(1);
        adj.get(1).add(2);

        List<Integer> order = TopologicalSort.sort(adj);

        assertEquals(Arrays.asList(0, 1, 2), order);
    }

    @Test
    void testSingleNode() {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(new ArrayList<>());
        List<Integer> order = TopologicalSort.sort(adj);
        assertEquals(Arrays.asList(0), order);
    }

    @Test
    void testComplexDAG() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 6; i++) adj.add(new ArrayList<>());

        adj.get(0).add(1);
        adj.get(0).add(2);
        adj.get(1).add(3);
        adj.get(2).add(3);
        adj.get(3).add(4);
        adj.get(3).add(5);

        List<Integer> order = TopologicalSort.sort(adj);

        assertEquals(6, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
        assertTrue(order.indexOf(3) < order.indexOf(4));
        assertTrue(order.indexOf(3) < order.indexOf(5));
    }

    @Test
    void testWeightedTopoSort() {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new int[]{1, 5});
        adj.get(1).add(new int[]{2, 3});

        List<Integer> order = TopologicalSort.sortWeighted(adj);

        assertEquals(Arrays.asList(0, 1, 2), order);
    }

    @Test
    void testCycleDetection() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(1);
        adj.get(1).add(2);
        adj.get(2).add(0);

        assertThrows(IllegalStateException.class, () -> {
            TopologicalSort.sort(adj);
        });
    }
}




