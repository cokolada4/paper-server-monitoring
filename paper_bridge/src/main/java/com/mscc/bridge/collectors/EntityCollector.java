package com.mscc.bridge.collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.*;

public class EntityCollector {

    public List<Map<String, Object>> collectEntities() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            Map<String, Object> worldData = new HashMap<>();
            worldData.put("world_name", world.getName());

            int living = 0;
            int hostile = 0;
            int passive = 0;
            int items = 0;
            int vehicles = 0;
            Map<String, Integer> typeCounts = new HashMap<>();

            Collection<Entity> entities = world.getEntities();
            worldData.put("total_entities", entities.size());

            for (Entity e : entities) {
                String typeName = e.getType().name();
                typeCounts.put(typeName, typeCounts.getOrDefault(typeName, 0) + 1);

                if (e instanceof LivingEntity) {
                    living++;
                    if (e instanceof Monster) {
                        hostile++;
                    } else if (e instanceof Animals || e instanceof Villager || e instanceof WaterMob) {
                        passive++;
                    }
                } else if (e instanceof Item) {
                    items++;
                } else if (e instanceof Vehicle) {
                    vehicles++;
                }
            }

            worldData.put("living_entities", living);
            worldData.put("hostile_mobs", hostile);
            worldData.put("passive_mobs", passive);
            worldData.put("items", items);
            worldData.put("vehicles", vehicles);
            worldData.put("block_entities", world.getTileEntityCount());
            worldData.put("counts_by_type", typeCounts);

            result.add(worldData);
        }

        return result;
    }
}
