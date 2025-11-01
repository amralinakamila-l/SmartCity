package org.example.graph.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;

public class ResultSaver {
    public static void saveJson(Object result, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(result, writer);
            System.out.println("Saved result to " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
