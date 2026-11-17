package com.mscc.bridge.collectors;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventCollector implements Listener {

    private final ConcurrentLinkedQueue<Map<String, Object>> eventQueue = new ConcurrentLinkedQueue<>();

    public Queue<Map<String, Object>> getEventQueue() {
        return eventQueue;
    }

    public List<Map<String, Object>> drainEvents() {
        List<Map<String, Object>> drained = new ArrayList<>();
        Map<String, Object> event;
        while ((event = eventQueue.poll()) != null) {
            drained.add(event);
        }
        return drained;
    }

    private void enqueue(String type, String uuid, String name, Map<String, Object> details) {
        Map<String, Object> map = new HashMap<>();
        map.put("event_id", UUID.randomUUID().toString());
        map.put("timestamp", Instant.now().toString());
        map.put("event_type", type);
        map.put("player_uuid", uuid);
        map.put("player_name", name);
        map.put("details", details != null ? details : Collections.emptyMap());
        eventQueue.offer(map);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Map<String, Object> details = new HashMap<>();
        details.put("world", event.getPlayer().getWorld().getName());
        enqueue("player_join", event.getPlayer().getUniqueId().toString(), event.getPlayer().getName(), details);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Map<String, Object> details = new HashMap<>();
        details.put("world", event.getPlayer().getWorld().getName());
        enqueue("player_quit", event.getPlayer().getUniqueId().toString(), event.getPlayer().getName(), details);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Map<String, Object> details = new HashMap<>();
        details.put("death_message", event.getDeathMessage());
        enqueue("player_death", event.getEntity().getUniqueId().toString(), event.getEntity().getName(), details);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Map<String, Object> details = new HashMap<>();
        details.put("from_world", event.getFrom().getName());
        details.put("to_world", event.getPlayer().getWorld().getName());
        enqueue("world_change", event.getPlayer().getUniqueId().toString(), event.getPlayer().getName(), details);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        String key = event.getAdvancement().getKey().toString();
        // Skip hidden recipes / recipes advancement spam
        if (key.startsWith("minecraft:recipes/")) return;

        Map<String, Object> details = new HashMap<>();
        details.put("advancement_key", key);
        enqueue("advancement", event.getPlayer().getUniqueId().toString(), event.getPlayer().getName(), details);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
        Map<String, Object> details = new HashMap<>();
        details.put("plugin_name", event.getPlugin().getName());
        enqueue("plugin_enable", null, null, details);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        Map<String, Object> details = new HashMap<>();
        details.put("plugin_name", event.getPlugin().getName());
        enqueue("plugin_disable", null, null, details);
    }
}
