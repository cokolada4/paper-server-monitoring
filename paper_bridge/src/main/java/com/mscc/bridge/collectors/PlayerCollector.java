package com.mscc.bridge.collectors;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerCollector {

    public List<Map<String, Object>> collectOnlinePlayers() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            Map<String, Object> data = new HashMap<>();
            data.put("uuid", p.getUniqueId().toString());
            data.put("username", p.getName());
            data.put("current_world", p.getWorld().getName());
            data.put("dimension", p.getWorld().getEnvironment().name());
            data.put("x", Math.round(p.getLocation().getX() * 10.0) / 10.0);
            data.put("y", Math.round(p.getLocation().getY() * 10.0) / 10.0);
            data.put("z", Math.round(p.getLocation().getZ() * 10.0) / 10.0);
            data.put("ping", p.getPing());
            data.put("gamemode", p.getGameMode().name());
            data.put("health", Math.round(p.getHealth() * 10.0) / 10.0);
            data.put("food_level", p.getFoodLevel());
            data.put("exp_level", p.getLevel());
            data.put("exp_total", p.getTotalExperience());

            try {
                data.put("death_count", p.getStatistic(Statistic.DEATHS));
                data.put("player_kills", p.getStatistic(Statistic.PLAYER_KILLS));
                data.put("mob_kills", p.getStatistic(Statistic.MOB_KILLS));
                data.put("playtime_ticks", p.getStatistic(Statistic.PLAY_ONE_MINUTE));
            } catch (Throwable ignored) {
                data.put("death_count", 0);
                data.put("player_kills", 0);
                data.put("mob_kills", 0);
                data.put("playtime_ticks", 0);
            }

            list.add(data);
        }
        return list;
    }
}
