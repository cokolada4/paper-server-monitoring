package com.mscc.bridge.collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class WorldCollector {

    public List<Map<String, Object>> collectWorlds() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (World w : Bukkit.getWorlds()) {
            Map<String, Object> data = new HashMap<>();
            data.put("world_name", w.getName());
            data.put("environment", w.getEnvironment().name());
            data.put("world_uuid", w.getUID().toString());
            data.put("world_time", w.getTime());

            String weather = "CLEAR";
            if (w.isThundering()) {
                weather = "THUNDER";
            } else if (w.hasStorm()) {
                weather = "RAIN";
            }
            data.put("weather", weather);
            data.put("difficulty", w.getDifficulty().name());
            data.put("border_size", w.getWorldBorder().getSize());
            data.put("online_players", w.getPlayerCount());
            data.put("loaded_chunks", w.getLoadedChunks().length);

            // Paper API provides entity counts efficiently without iterating all entities
            int totalEntities = 0;
            try {
                totalEntities = w.getEntityCount();
            } catch (Throwable t) {
                totalEntities = w.getEntities().size();
            }
            data.put("entity_count", totalEntities);

            int livingCount = 0;
            try {
                livingCount = w.getLivingEntityCount();
            } catch (Throwable t) {
                for (org.bukkit.entity.Entity e : w.getEntities()) {
                    if (e instanceof LivingEntity) livingCount++;
                }
            }
            data.put("living_entity_count", livingCount);
            data.put("block_entity_count", w.getTileEntityCount());

            list.add(data);
        }
        return list;
    }
}
