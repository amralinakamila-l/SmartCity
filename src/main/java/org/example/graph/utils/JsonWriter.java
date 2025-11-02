package org.example.graph.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.model.AlgorithmResult;

import java.io.FileWriter;
import java.io.IOException;

public class JsonWriter {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting() // делает JSON читаемым
            .create();

    public static void writeResult(AlgorithmResult result, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(result, writer);
            System.out.println("Result saved to: " + filePath);
        } catch (IOException e) {
            System.err.println(" Error saving file: " + e.getMessage());
        }
    }
}

