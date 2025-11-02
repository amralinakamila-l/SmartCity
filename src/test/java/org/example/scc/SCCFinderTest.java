package org.example.scc;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SCCFinderTest {

    @Test
    void testSingleSCC() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(1);
        adj.get(1).add(2);
        adj.get(2).add(0);

        SCCFinder finder = new SCCFinder(adj);
        List<List<Integer>> sccs = finder.findSCCs();

        assertEquals(1, sccs.size());
        assertTrue(sccs.get(0).containsAll(Arrays.asList(0,1,2)));
    }

    @Test
    void testMultipleSCCs() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 4; i++) adj.add(new ArrayList<>());
        adj.get(0).add(1);
        adj.get(1).add(0);
        adj.get(2).add(3);

        SCCFinder finder = new SCCFinder(adj);
        List<List<Integer>> sccs = finder.findSCCs();

        assertEquals(3, sccs.size());
    }

    @Test
    void testEmptyGraph() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());

        SCCFinder finder = new SCCFinder(adj);
        List<List<Integer>> sccs = finder.findSCCs();

        assertEquals(3, sccs.size());
    }

    @Test
    void testSingleNode() {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(new ArrayList<>());

        SCCFinder finder = new SCCFinder(adj);
        List<List<Integer>> sccs = finder.findSCCs();

        assertEquals(1, sccs.size());
        assertEquals(1, sccs.get(0).size());
        assertEquals(0, sccs.get(0).get(0));
    }

    @Test
    void testDisconnectedComponents() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 6; i++) adj.add(new ArrayList<>());

        adj.get(0).add(1);
        adj.get(1).add(2);
        adj.get(2).add(0);
        adj.get(3).add(4);
        adj.get(4).add(5);
        adj.get(5).add(3);

        SCCFinder finder = new SCCFinder(adj);
        List<List<Integer>> sccs = finder.findSCCs();

        assertEquals(2, sccs.size());
        assertEquals(3, sccs.get(0).size());
        assertEquals(3, sccs.get(1).size());
    }
}





