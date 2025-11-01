package org.example.metrics;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
    private Map<String, Integer> counters = new HashMap<>();
    private Map<String, Long> timers = new HashMap<>();
    private long startTime;

    public Metrics() {
        this.startTime = System.nanoTime();
    }

    // 🔹 Запускаем измерение времени
    public void start() {
        this.startTime = System.nanoTime();
    }
    public void stop() {
        this.startTime = System.nanoTime();
    }

    // 🔹 Увеличиваем счётчик операций
    public void increment(String key) {
        counters.put(key, counters.getOrDefault(key, 0) + 1);
    }

    // 🔹 Регистрируем время выполнения операции
    public void recordTime(String key, long startNs) {
        long duration = System.nanoTime() - startNs;
        timers.put(key, timers.getOrDefault(key, 0L) + duration);
    }

    // 🔹 Возвращаем время в миллисекундах
    public double getTimeMs(String key) {
        return timers.containsKey(key) ? timers.get(key) / 1_000_000.0 : 0.0;
    }

    // 🔹 Печать короткого отчёта
    public void print(String label) {
        long totalMs = (System.nanoTime() - startTime) / 1_000_000;
        System.out.println("\n[" + label + "] Metrics:");
        for (var e : counters.entrySet()) {
            System.out.printf("  %s: %d\n", e.getKey(), e.getValue());
        }
        for (var e : timers.entrySet()) {
            System.out.printf("  %s: %.3f ms\n", e.getKey(), getTimeMs(e.getKey()));
        }
        System.out.printf("  Total time: %d ms\n", totalMs);
    }

    // 🔹 Подробный итоговый отчёт (для Main)
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n==== Metrics Summary ====\n");

        if (counters.isEmpty() && timers.isEmpty()) {
            sb.append("No metrics recorded.\n");
            return sb.toString();
        }

        sb.append("-- Counters --\n");
        for (var e : counters.entrySet()) {
            sb.append(String.format("%s: %d\n", e.getKey(), e.getValue()));
        }

        sb.append("-- Timings (ms) --\n");
        for (var e : timers.entrySet()) {
            sb.append(String.format("%s: %.3f ms\n", e.getKey(), getTimeMs(e.getKey())));
        }

        long totalMs = (System.nanoTime() - startTime) / 1_000_000;
        sb.append(String.format("Total runtime: %d ms\n", totalMs));

        return sb.toString();
    }
}


