package org.example.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.graph.Edge;
import org.example.graph.GraphData;

import java.io.FileWriter;
import java.util.*;

public class DataGenerator {

    private static Random rnd = new Random(42);

    public static void genSimple(String path, int n, double density, boolean directed, boolean forceCycles) throws Exception {
        List<Edge> edges = new ArrayList<>();
        int maxEdges = (int)(n * (n-1) * density);
        // optionally add a cycle
        if (forceCycles && n >= 3) {
            for (int i = 0; i < 3; i++) edges.add(new Edge(i, (i+1)%3, rnd.nextInt(5)+1));
        }
        while (edges.size() < maxEdges) {
            int u = rnd.nextInt(n), v = rnd.nextInt(n);
            if (u==v) continue;
            edges.add(new Edge(u,v,rnd.nextInt(5)+1));
        }
        GraphData g = new GraphData();
        g.directed = directed; g.n = n; g.edges = edges; g.source = 0; g.weight_model = "edge";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fw = new FileWriter(path)) {
            gson.toJson(g, fw);
        }
    }
    public static void main(String[] args) throws Exception {
        // Small (6–10 nodes)
        genSimple("data/input/small1.json", 6, 0.3, false, false);
        genSimple("data/input/small2.json", 8, 0.4, false, true);
        genSimple("data/input/small3.json", 10, 0.5, true, false);

        // Medium (10–20 nodes)
        genSimple("data/input/medium1.json", 12, 0.3, false, true);
        genSimple("data/input/medium2.json", 15, 0.4, true, true);
        genSimple("data/input/medium3.json", 18, 0.5, false, false);

        // Large (20–50 nodes)
        genSimple("data/large1.json", 25, 0.3, false, true);
        genSimple("data/large2.json", 35, 0.4, true, false);
        genSimple("data/large3.json", 50, 0.5, false, false);

        System.out.println(" 9 graphs successfully generated in /data folder!");
    }


}
