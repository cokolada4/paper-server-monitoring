package com.mscc.bridge.collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.*;

public class PluginCollector {

    public List<Map<String, Object>> collectPlugins() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            Map<String, Object> data = new HashMap<>();
            PluginDescriptionFile desc = p.getDescription();
            data.put("name", p.getName());
            data.put("version", desc.getVersion());
            data.put("authors", desc.getAuthors());
            data.put("enabled", p.isEnabled());
            data.put("dependencies", desc.getDepend());
            data.put("soft_dependencies", desc.getSoftDepend());
            data.put("website", desc.getWebsite());
            list.add(data);
        }
        return list;
    }
}
