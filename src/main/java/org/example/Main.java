package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.dagsp.DAGShortestPaths;
import org.example.graph.GraphData;
import org.example.graph.utils.GraphBuilder;
import org.example.metrics.Metrics;
import org.example.scc.CondensationBuilder;
import org.example.scc.SCCFinder;
import org.example.topo.TopologicalSort;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class Main {

    private static final String[] INPUT_FILES = {
            "data/input/small1.json", "data/input/small2.json", "data/input/small3.json",
            "data/input/medium1.json", "data/input/medium2.json", "data/input/medium3.json",
            "data/input/large1.json", "data/input/large2.json", "data/input/large3.json"
    };

    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File outputDir = new File("data/output");
        if (!outputDir.exists()) outputDir.mkdirs();

        File csvFile = new File("data/results.csv");

        // Настройка форматирования чисел для CSV (используем точку как разделитель)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat timeFormat = new DecimalFormat("0.000", symbols);

        // 🔥 ПРОГРЕВ JVM - запускаем один раз перед основными измерениями
        System.out.println("Warming up JVM...");
        warmUpJVM(gson);
        System.out.println("Warm up completed.\n");

        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
            pw.println("inputFile,numNodes,numEdges,numSCCs,sizes,topoOrder,shortestDistances,longestPathLength,longestPath,totalTimeMs");

            for (String inputFile : INPUT_FILES) {
                System.out.println("\n=== Processing: " + inputFile + " ===");

                // Загрузка графа (не включаем в измерение времени)
                GraphData graph = gson.fromJson(new FileReader(inputFile), GraphData.class);
                List<List<Integer>> adj = GraphBuilder.buildAdjList(graph);

                // Начинаем измерение времени ТОЛЬКО для алгоритмов
                Metrics pipelineMetrics = new Metrics();
                pipelineMetrics.start();

                // --- SCC
                SCCFinder sccFinder = new SCCFinder(adj);
                List<List<Integer>> sccs = sccFinder.findSCCs();

                System.out.println("Found " + sccs.size() + " SCCs:");
                for (int i = 0; i < sccs.size(); i++) {
                    System.out.println("  SCC " + i + ": " + sccs.get(i).size() + " nodes");
                }

                // --- Build condensation DAG
                CondensationBuilder.Result condensation = CondensationBuilder.build(sccs, adj, graph);
                List<List<int[]>> weightedDAG = condensation.dagAdj;

                System.out.println("Condensation DAG has " + weightedDAG.size() + " components");

                // --- Topological order on weighted DAG
                List<Integer> topoOrder = TopologicalSort.sortWeighted(weightedDAG);
                System.out.println("Topological order: " + topoOrder);

                // --- Shortest paths on weighted DAG (from component 0)
                var shortestResult = DAGShortestPaths.findShortestPathsWeighted(weightedDAG, 0);
                List<Double> spDist = new ArrayList<>();
                for (double d : shortestResult.dist) {
                    spDist.add(d == Double.POSITIVE_INFINITY ? -1 : d);
                }
                System.out.println("Shortest paths from component 0: " + spDist);

                // --- Longest path on weighted DAG
                var longestResult = DAGShortestPaths.findLongestPathWeighted(weightedDAG);
                System.out.println("Longest path length: " + longestResult.longestLength);
                System.out.println("Longest path: " + longestResult.longestPath);

                // --- Завершаем измерение времени
                pipelineMetrics.stop();

                double totalTimeMs = pipelineMetrics.getTimeMs("total");
                System.out.printf("Total algorithm time: %.3f ms\n", totalTimeMs);

                // --- JSON Output
                Map<String, Object> output = new LinkedHashMap<>();
                output.put("inputFile", new File(inputFile).getName());
                output.put("numNodes", graph.n);
                output.put("numEdges", graph.edges.size());
                output.put("SCCs_num", sccs.size());
                output.put("SCCs_sizes", sccs.stream().map(List::size).toList());
                output.put("CondensationNodes", weightedDAG.size());
                output.put("TopologicalOrder", topoOrder);
                output.put("ShortestPathsFromComponent0", spDist);
                output.put("LongestPathLength", longestResult.longestLength);
                output.put("LongestPathComponents", longestResult.longestPath);
                output.put("TimingMs", totalTimeMs);

                String outFile = "data/output/" + new File(inputFile).getName().replace(".json", "_output.json");
                try (FileWriter fw = new FileWriter(outFile)) {
                    gson.toJson(output, fw);
                }

                // --- CSV Output - правильное форматирование
                // Экранируем списки, которые содержат запятые
                String sizesStr = "\"" + sccs.stream().map(List::size).toList().toString() + "\"";
                String topoStr = "\"" + topoOrder.toString() + "\"";
                String shortestStr = "\"" + spDist.toString() + "\"";
                String longestPathStr = "\"" + (longestResult.longestPath != null ? longestResult.longestPath.toString() : "[]") + "\"";

                pw.printf("%s,%d,%d,%d,%s,%s,%s,%.1f,%s,%s%n",
                        inputFile,
                        graph.n,
                        graph.edges.size(),
                        sccs.size(),
                        sizesStr,
                        topoStr,
                        shortestStr,
                        longestResult.longestLength,
                        longestPathStr,
                        timeFormat.format(totalTimeMs)
                );

                System.out.println("Saved: " + outFile);

                // Небольшая пауза между обработкой файлов
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
        }

        System.out.println("\nCSV saved to " + csvFile.getAbsolutePath());
        System.out.println("All graphs processed successfully!");
    }

    // 🔥 Метод для прогрева JVM
    private static void warmUpJVM(Gson gson) throws Exception {
        // Используем первый файл для прогрева
        String warmupFile = "data/input/small1.json";
        GraphData graph = gson.fromJson(new FileReader(warmupFile), GraphData.class);
        List<List<Integer>> adj = GraphBuilder.buildAdjList(graph);

        // Запускаем несколько раз все алгоритмы
        for (int i = 0; i < 3; i++) {
            SCCFinder sccFinder = new SCCFinder(adj);
            List<List<Integer>> sccs = sccFinder.findSCCs();
            CondensationBuilder.Result condensation = CondensationBuilder.build(sccs, adj, graph);
            List<List<int[]>> weightedDAG = condensation.dagAdj;
            TopologicalSort.sortWeighted(weightedDAG);
            DAGShortestPaths.findShortestPathsWeighted(weightedDAG, 0);
            DAGShortestPaths.findLongestPathWeighted(weightedDAG);
        }
    }
}











