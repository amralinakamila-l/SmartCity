package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.graph.GraphData;
import org.example.graph.utils.GraphBuilder;
import org.example.metrics.Metrics;
import org.example.scc.SCCFinder;
import org.example.topo.TopologicalSort;
import org.example.dagsp.DAGShortestPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Main {

    private static final String[] INPUT_FILES = {
            "data/small1.json", "data/small2.json", "data/small3.json",
            "data/medium1.json", "data/medium2.json", "data/medium3.json",
            "data/large1.json", "data/large2.json", "data/large3.json"
    };

    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File outputDir = new File("data/output");
        if (!outputDir.exists()) outputDir.mkdirs();

        for (String inputFile : INPUT_FILES) {
            System.out.println("Processing: " + inputFile);

            GraphData graph = gson.fromJson(new FileReader(inputFile), GraphData.class);
            var adj = GraphBuilder.buildAdjList(graph);

            // --- METRICS
            Metrics totalMetrics = new Metrics();

            // --- SCC
            totalMetrics.start();
            SCCFinder sccFinder = new SCCFinder(adj);
            List<List<Integer>> sccs = sccFinder.findSCCs();
            totalMetrics.stop();

            // Map node → SCC index
            Map<Integer, Integer> nodeToSCC = new HashMap<>();
            for (int i = 0; i < sccs.size(); i++)
                for (int v : sccs.get(i)) nodeToSCC.put(v, i);

            // --- Condensation DAG
            List<List<Integer>> dag = new ArrayList<>();
            for (int i = 0; i < sccs.size(); i++) dag.add(new ArrayList<>());

            for (int u = 0; u < adj.size(); u++) {
                for (int v : adj.get(u)) {
                    int su = nodeToSCC.get(u);
                    int sv = nodeToSCC.get(v);
                    if (su != sv && !dag.get(su).contains(sv)) dag.get(su).add(sv);
                }
            }

            // --- Topological Sort
            totalMetrics.start();
            List<Integer> topoOrder = TopologicalSort.sort(dag);
            totalMetrics.stop();

            // --- Shortest Paths in DAG
            totalMetrics.start();
            DAGShortestPaths.Result spResult = DAGShortestPaths.findShortestPaths(dag, 0);
            totalMetrics.stop();

            // --- Longest Path
            totalMetrics.start();
            DAGShortestPaths.Result lpResult = DAGShortestPaths.findLongestPath(dag);
            totalMetrics.stop();

            // --- Save results
            Map<String, Object> output = new LinkedHashMap<>();
            output.put("inputFile", new File(inputFile).getName());
            output.put("numNodes", graph.n);
            output.put("numEdges", graph.edges.size());

            Map<String, Object> sccInfo = new LinkedHashMap<>();
            sccInfo.put("numSCCs", sccs.size());
            sccInfo.put("components", sccs);
            List<Integer> sizes = sccs.stream().map(List::size).toList();
            sccInfo.put("sizes", sizes);
            output.put("SCC", sccInfo);

            Map<String, Object> dagInfo = new LinkedHashMap<>();
            dagInfo.put("numNodes", dag.size());
            dagInfo.put("edges", dag);
            output.put("CondensationDAG", dagInfo);

            output.put("TopologicalOrder", topoOrder);
            output.put("ShortestPaths", spResult);
            output.put("LongestPath", lpResult);
            output.put("Metrics", totalMetrics.summary());

            String outFile = "data/output/" + new File(inputFile).getName().replace(".json", "_output.json");
            try (FileWriter fw = new FileWriter(outFile)) {
                gson.toJson(output, fw);
            }

            System.out.println("✅ Saved: " + outFile + "\n");
        }

        System.out.println("All graphs processed successfully.");
    }
}


