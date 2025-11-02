package org.example.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.graph.Edge;
import org.example.graph.GraphData;

import java.io.FileWriter;
import java.util.*;

public class DataGenerator {

    private static Random rnd = new Random(42);

    public static void genDAG(String path, int n, double density, boolean forceDisconnected) throws Exception {
        List<Edge> edges = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (rnd.nextDouble() < density) {
                    edges.add(new Edge(u, v, rnd.nextInt(5) + 1));
                }
            }
        }

        if (forceDisconnected && n >= 6) {
            int splitPoint = n / 2;
            edges.removeIf(e -> e.u < splitPoint && e.v >= splitPoint);
        }

        GraphData g = new GraphData();
        g.directed = true;
        g.n = n;
        g.edges = edges;
        g.source = 0;
        g.weight_model = "edge";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fw = new FileWriter(path)) {
            gson.toJson(g, fw);
        }
    }

    public static void genSimple(String path, int n, double density, boolean directed, boolean forceCycles) throws Exception {
        genDAG(path, n, density, forceCycles);
    }

    public static void main(String[] args) throws Exception {
        genDAG("data/input/small1.json", 8, 0.3, false);
        genDAG("data/input/small2.json", 10, 0.4, true);
        genDAG("data/input/small3.json", 12, 0.5, false);

        genDAG("data/input/medium1.json", 15, 0.3, true);
        genDAG("data/input/medium2.json", 18, 0.4, false);
        genDAG("data/input/medium3.json", 20, 0.5, true);

        genDAG("data/input/large1.json", 30, 0.3, false);
        genDAG("data/input/large2.json", 40, 0.4, true);
        genDAG("data/input/large3.json", 50, 0.5, false);

        System.out.println("9 DAG graphs successfully generated!");
    }
}