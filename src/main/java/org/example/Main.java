package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
        File excelFile = new File("data/results.xlsx");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat timeFormat = new DecimalFormat("0.000", symbols);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Graph Analysis Results");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setAlignment(HorizontalAlignment.RIGHT);

        String[] headers = {
                "Input File", "Nodes", "Edges", "SCC Count", "SCC Sizes",
                "Topological Order", "Shortest Paths", "Longest Path Length",
                "Longest Path", "Time (ms)"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int excelRowNum = 1;

        System.out.println("Warming up JVM...");
        warmUpJVM(gson);
        System.out.println("Warm up completed.\n");

        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
            pw.println("inputFile,numNodes,numEdges,numSCCs,sizes,topoOrder,shortestDistances,longestPathLength,longestPath,totalTimeMs");

            for (String inputFile : INPUT_FILES) {
                System.out.println("\n=== Processing: " + inputFile + " ===");

                GraphData graph = gson.fromJson(new FileReader(inputFile), GraphData.class);
                List<List<Integer>> adj = GraphBuilder.buildAdjList(graph);

                Metrics pipelineMetrics = new Metrics();
                pipelineMetrics.start();

                SCCFinder sccFinder = new SCCFinder(adj);
                List<List<Integer>> sccs = sccFinder.findSCCs();

                System.out.println("Found " + sccs.size() + " SCCs:");
                for (int i = 0; i < sccs.size(); i++) {
                    System.out.println("  SCC " + i + ": " + sccs.get(i).size() + " nodes");
                }

                CondensationBuilder.Result condensation = CondensationBuilder.build(sccs, adj, graph);
                List<List<int[]>> weightedDAG = condensation.dagAdj;
                System.out.println("Condensation DAG has " + weightedDAG.size() + " components");

                List<Integer> topoOrder = TopologicalSort.sortWeighted(weightedDAG);
                System.out.println("Topological order: " + topoOrder);

                var shortestResult = DAGShortestPaths.findShortestPathsWeighted(weightedDAG, 0);
                List<Double> spDist = new ArrayList<>();
                for (double d : shortestResult.dist) {
                    spDist.add(d == Double.POSITIVE_INFINITY ? -1 : d);
                }
                System.out.println("Shortest paths from component 0: " + spDist);

                var longestResult = DAGShortestPaths.findLongestPathWeighted(weightedDAG);
                System.out.println("Longest path length: " + longestResult.longestLength);
                System.out.println("Longest path: " + longestResult.longestPath);

                pipelineMetrics.stop();
                double totalTimeMs = pipelineMetrics.getTimeMs("total");
                System.out.printf("Total algorithm time: %.3f ms\n", totalTimeMs);
                Row dataRow = sheet.createRow(excelRowNum++);

                dataRow.createCell(0).setCellValue(new File(inputFile).getName());
                dataRow.createCell(1).setCellValue(graph.n);
                dataRow.createCell(2).setCellValue(graph.edges.size());
                dataRow.createCell(3).setCellValue(sccs.size());
                dataRow.createCell(4).setCellValue(sccs.stream().map(List::size).toList().toString());
                dataRow.createCell(5).setCellValue(topoOrder.toString());
                dataRow.createCell(6).setCellValue(spDist.toString());
                dataRow.createCell(7).setCellValue(longestResult.longestLength);
                dataRow.createCell(8).setCellValue(longestResult.longestPath != null ?
                        longestResult.longestPath.toString() : "[]");
                dataRow.createCell(9).setCellValue(totalTimeMs);

                String sizesStr = "\"" + sccs.stream().map(List::size).toList().toString() + "\"";
                String topoStr = "\"" + topoOrder.toString() + "\"";
                String shortestStr = "\"" + spDist.toString() + "\"";
                String longestPathStr = "\"" + (longestResult.longestPath != null ?
                        longestResult.longestPath.toString() : "[]") + "\"";

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

                System.out.println("Processed: " + inputFile);
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }
        }
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
            workbook.write(fileOut);
            System.out.println("\nExcel file created: " + excelFile.getAbsolutePath());
        }
        workbook.close();

        System.out.println("CSV saved to: " + csvFile.getAbsolutePath());
        System.out.println("All graphs processed successfully!");
    }

    private static void warmUpJVM(Gson gson) throws Exception {
        String warmupFile = "data/input/small1.json";
        GraphData graph = gson.fromJson(new FileReader(warmupFile), GraphData.class);
        List<List<Integer>> adj = GraphBuilder.buildAdjList(graph);

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











