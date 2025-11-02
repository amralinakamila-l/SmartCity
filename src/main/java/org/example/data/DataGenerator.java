package org.example.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.graph.Edge;
import org.example.graph.GraphData;

import java.io.FileWriter;
import java.util.*;

public class DataGenerator {

    private static Random rnd = new Random(42);

    public static void genGraphWithSCCs(String path, int n, double density, int minSCC, int maxSCC) throws Exception {
        // 1. Определяем количество компонент k
        int k = rnd.nextInt(maxSCC - minSCC + 1) + minSCC;
        k = Math.min(k, n);

        // 2. Разбиваем n вершин на k компонент
        List<List<Integer>> components = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            components.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            int compIndex = rnd.nextInt(k);
            components.get(compIndex).add(i);
        }
        // Удаляем пустые компоненты
        components.removeIf(List::isEmpty);
        k = components.size();

        // 3. Для каждой компоненты, генерируем цикл (если размер >= 2)
        List<Edge> edges = new ArrayList<>();
        for (List<Integer> comp : components) {
            int size = comp.size();
            if (size == 1) {
                continue;
            }
            // Создаем цикл: каждая вершина указывает на следующую, последняя на первую
            for (int i = 0; i < size; i++) {
                int u = comp.get(i);
                int v = comp.get((i+1) % size);
                edges.add(new Edge(u, v, rnd.nextInt(5) + 1));
            }
        }

        // 4. Добавляем случайные ребра между компонентами (только от меньшего индекса компоненты к большему)
        for (int i = 0; i < k; i++) {
            for (int j = i+1; j < k; j++) {
                for (int u : components.get(i)) {
                    for (int v : components.get(j)) {
                        if (rnd.nextDouble() < density) {
                            edges.add(new Edge(u, v, rnd.nextInt(5) + 1));
                        }
                    }
                }
            }
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

    public static void main(String[] args) throws Exception {
        // Small (6–10 nodes)
        genGraphWithSCCs("data/input/small1.json", 6, 0.3, 2, 3);
        genGraphWithSCCs("data/input/small2.json", 8, 0.4, 2, 3);
        genGraphWithSCCs("data/input/small3.json", 10, 0.5, 2, 4);

        // Medium (10–20 nodes)
        genGraphWithSCCs("data/input/medium1.json", 12, 0.3, 3, 5);
        genGraphWithSCCs("data/input/medium2.json", 15, 0.4, 3, 6);
        genGraphWithSCCs("data/input/medium3.json", 18, 0.5, 4, 7);

        // Large (20–50 nodes)
        genGraphWithSCCs("data/input/large1.json", 25, 0.3, 5, 8);
        genGraphWithSCCs("data/input/large2.json", 35, 0.4, 6, 10);
        genGraphWithSCCs("data/input/large3.json", 50, 0.5, 7, 12);

        System.out.println(" 9 graphs successfully generated in /data folder!");
    }
}