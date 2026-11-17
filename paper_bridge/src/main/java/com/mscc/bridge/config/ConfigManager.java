package com.mscc.bridge.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private String host;
    private int port;
    private String authToken;
    private int performanceInterval;
    private int playerInterval;
    private int worldInterval;
    private int entityInterval;
    private int pluginInterval;
    private boolean allowRemoteCommands;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        this.load();
    }

    public void load() {
        this.plugin.reloadConfig();
        FileConfiguration config = this.plugin.getConfig();
        this.host = config.getString("connection.host", "127.0.0.1");
        this.port = config.getInt("connection.port", 8765);
        this.authToken = config.getString("connection.authentication_token", "CHANGE_ME");
        this.performanceInterval = config.getInt("collection.performance_interval", 20);
        this.playerInterval = config.getInt("collection.player_interval", 40);
        this.worldInterval = config.getInt("collection.world_interval", 200);
        this.entityInterval = config.getInt("collection.entity_interval", 300);
        this.pluginInterval = config.getInt("collection.plugin_interval", 600);
        this.allowRemoteCommands = config.getBoolean("security.allow_remote_commands", true);
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getAuthToken() { return authToken; }
    public int getPerformanceInterval() { return performanceInterval; }
    public int getPlayerInterval() { return playerInterval; }
    public int getWorldInterval() { return worldInterval; }
    public int getEntityInterval() { return entityInterval; }
    public int getPluginInterval() { return pluginInterval; }
    public boolean isAllowRemoteCommands() { return allowRemoteCommands; }
}
