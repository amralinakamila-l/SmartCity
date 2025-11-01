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
}




