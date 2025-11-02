package org.example.metrics;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
    private Map<String, Integer> counters = new HashMap<>();
    private Map<String, Long> timers = new HashMap<>();
    private long startTime;
    private boolean running = false;

    public Metrics() {
        // Конструктор без автоматического старта
    }

    // 🔹 Запускаем измерение времени
    public void start() {
        if (running) {
            System.err.println("Metrics already running! Stopping current measurement.");
            stop();
        }
        this.startTime = System.nanoTime();
        this.running = true;
    }

    public void stop() {
        if (running) {
            long duration = System.nanoTime() - startTime;
            // Накопляем время, а не заменяем
            long currentTotal = timers.getOrDefault("total", 0L);
            timers.put("total", currentTotal + duration);
            running = false;
        }
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
        System.out.println("\n[" + label + "] Metrics:");
        for (var e : counters.entrySet()) {
            System.out.printf("  %s: %d\n", e.getKey(), e.getValue());
        }
        for (var e : timers.entrySet()) {
            System.out.printf("  %s: %.3f ms\n", e.getKey(), getTimeMs(e.getKey()));
        }
        if (timers.containsKey("total")) {
            System.out.printf("  Total time: %.3f ms\n", getTimeMs("total"));
        }
    }

    // 🔹 Подробный итоговый отчёт (для Main)
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Metrics: ");

        boolean hasData = false;

        if (!counters.isEmpty()) {
            sb.append("Counters[");
            for (var e : counters.entrySet()) {
                sb.append(String.format("%s=%d, ", e.getKey(), e.getValue()));
            }
            sb.setLength(sb.length() - 2); // remove last ", "
            sb.append("] ");
            hasData = true;
        }

        if (!timers.isEmpty()) {
            sb.append("Timings[");
            for (var e : timers.entrySet()) {
                sb.append(String.format("%s=%.3fms, ", e.getKey(), getTimeMs(e.getKey())));
            }
            sb.setLength(sb.length() - 2); // remove last ", "
            sb.append("]");
            hasData = true;
        }

        if (!hasData) {
            sb.append("No metrics collected");
        }

        return sb.toString();
    }

    // 🔹 Получить общее время в наносекундах
    public long getTotalTimeNs() {
        return timers.getOrDefault("total", 0L);
    }

    // 🔹 Очистить метрики
    public void clear() {
        counters.clear();
        timers.clear();
        running = false;
    }

    // 🔹 Проверить, запущены ли измерения
    public boolean isRunning() {
        return running;
    }
}


