package com.mscc.bridge;

import com.mscc.bridge.api.ApiServer;
import com.mscc.bridge.collectors.*;
import com.mscc.bridge.config.ConfigManager;
import com.mscc.bridge.security.TokenManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MSCCBridge extends JavaPlugin {

    private ConfigManager configManager;
    private TokenManager tokenManager;
    private PerformanceCollector performanceCollector;
    private PlayerCollector playerCollector;
    private WorldCollector worldCollector;
    private EntityCollector entityCollector;
    private PluginCollector pluginCollector;
    private EventCollector eventCollector;
    private ApiServer apiServer;

    @Override
    public void onEnable() {
        getLogger().info("Initializing MSCC-Bridge for Paper 1.21.4...");

        // Load Configuration
        this.configManager = new ConfigManager(this);
        this.tokenManager = new TokenManager(configManager.getAuthToken());

        // Initialize Collectors
        this.performanceCollector = new PerformanceCollector();
        this.playerCollector = new PlayerCollector();
        this.worldCollector = new WorldCollector();
        this.entityCollector = new EntityCollector();
        this.pluginCollector = new PluginCollector();
        this.eventCollector = new EventCollector();

        // Register Event Listeners
        getServer().getPluginManager().registerEvents(this.eventCollector, this);

        // Start Localhost API Server
        this.apiServer = new ApiServer(this, this.tokenManager);
        try {
            this.apiServer.start(configManager.getHost(), configManager.getPort());
        } catch (IOException e) {
            getLogger().severe("Could not bind MSCC-Bridge API on port " + configManager.getPort() + ": " + e.getMessage());
        }

        getLogger().info("MSCC-Bridge enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down MSCC-Bridge...");
        if (this.apiServer != null) {
            this.apiServer.stop();
        }
        getLogger().info("MSCC-Bridge disabled.");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public PerformanceCollector getPerformanceCollector() { return performanceCollector; }
    public PlayerCollector getPlayerCollector() { return playerCollector; }
    public WorldCollector getWorldCollector() { return worldCollector; }
    public EntityCollector getEntityCollector() { return entityCollector; }
    public PluginCollector getPluginCollector() { return pluginCollector; }
    public EventCollector getEventCollector() { return eventCollector; }
}
