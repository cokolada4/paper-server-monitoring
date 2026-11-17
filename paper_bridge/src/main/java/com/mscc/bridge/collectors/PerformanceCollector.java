package com.mscc.bridge.collectors;

import org.bukkit.Bukkit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PerformanceCollector {

    public Map<String, Object> collectMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Paper TPS API
        try {
            double[] tps = Bukkit.getTPS();
            if (tps != null && tps.length >= 3) {
                metrics.put("tps_1m", Math.min(20.0, Math.round(tps[0] * 100.0) / 100.0));
                metrics.put("tps_5m", Math.min(20.0, Math.round(tps[1] * 100.0) / 100.0));
                metrics.put("tps_15m", Math.min(20.0, Math.round(tps[2] * 100.0) / 100.0));
                metrics.put("current_tps", metrics.get("tps_1m"));
            }
        } catch (Throwable t) {
            metrics.put("tps_1m", null);
            metrics.put("tps_5m", null);
            metrics.put("tps_15m", null);
        }

        // Paper MSPT API (Bukkit.getAverageTickTime() or Bukkit.getServer().getTickTimes())
        try {
            double avgTickTime = Bukkit.getAverageTickTime();
            metrics.put("mspt_avg", Math.round(avgTickTime * 100.0) / 100.0);

            long[] tickTimes = Bukkit.getServer().getTickTimes();
            if (tickTimes != null && tickTimes.length > 0) {
                double[] msArray = new double[tickTimes.length];
                double min = Double.MAX_VALUE;
                double max = 0;
                int spikes = 0;

                for (int i = 0; i < tickTimes.length; i++) {
                    double ms = tickTimes[i] / 1_000_000.0;
                    msArray[i] = ms;
                    if (ms < min) min = ms;
                    if (ms > max) max = ms;
                    if (ms > 50.0) spikes++;
                }

                Arrays.sort(msArray);
                metrics.put("mspt_min", Math.round(min * 100.0) / 100.0);
                metrics.put("mspt_max", Math.round(max * 100.0) / 100.0);
                metrics.put("mspt_p50", Math.round(getPercentile(msArray, 50) * 100.0) / 100.0);
                metrics.put("mspt_p95", Math.round(getPercentile(msArray, 95) * 100.0) / 100.0);
                metrics.put("mspt_p99", Math.round(getPercentile(msArray, 99) * 100.0) / 100.0);
                metrics.put("tick_spikes_count", spikes);
            }
        } catch (Throwable t) {
            metrics.put("mspt_avg", null);
            metrics.put("mspt_min", null);
            metrics.put("mspt_max", null);
            metrics.put("mspt_p50", null);
            metrics.put("mspt_p95", null);
            metrics.put("mspt_p99", null);
            metrics.put("tick_spikes_count", 0);
        }

        metrics.put("online_players", Bukkit.getOnlinePlayers().size());
        metrics.put("max_players", Bukkit.getMaxPlayers());
        metrics.put("loaded_worlds", Bukkit.getWorlds().size());

        return metrics;
    }

    private double getPercentile(double[] sortedArray, double percentile) {
        int index = (int) Math.ceil((percentile / 100.0) * sortedArray.length) - 1;
        return sortedArray[Math.max(0, Math.min(index, sortedArray.length - 1))];
    }
}
