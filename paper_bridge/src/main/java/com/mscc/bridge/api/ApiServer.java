package com.mscc.bridge.api;

import com.google.gson.Gson;
import com.mscc.bridge.MSCCBridge;
import com.mscc.bridge.security.TokenManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;

public class ApiServer {

    private final MSCCBridge plugin;
    private final TokenManager tokenManager;
    private final Gson gson = new Gson();
    private HttpServer server;

    public ApiServer(MSCCBridge plugin, TokenManager tokenManager) {
        this.plugin = plugin;
        this.tokenManager = tokenManager;
    }

    public void start(String host, int port) throws IOException {
        // Enforce binding to localhost only
        if (!"127.0.0.1".equals(host) && !"localhost".equalsIgnoreCase(host)) {
            host = "127.0.0.1";
        }

        server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.setExecutor(Executors.newFixedThreadPool(4));

        server.createContext("/health", exchange -> {
            sendResponse(exchange, 200, Map.of("status", "ok", "service", "MSCC-Bridge", "version", "1.0.0"));
        });

        server.createContext("/metrics", new AuthenticatedHandler(exchange -> {
            Map<String, Object> data = new HashMap<>();
            data.put("performance", plugin.getPerformanceCollector().collectMetrics());
            data.put("players", plugin.getPlayerCollector().collectOnlinePlayers());
            data.put("worlds", plugin.getWorldCollector().collectWorlds());
            data.put("entities", plugin.getEntityCollector().collectEntities());
            data.put("events", plugin.getEventCollector().drainEvents());
            sendResponse(exchange, 200, data);
        }));

        server.createContext("/plugins", new AuthenticatedHandler(exchange -> {
            sendResponse(exchange, 200, plugin.getPluginCollector().collectPlugins());
        }));

        server.createContext("/command", new AuthenticatedHandler(exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, Map.of("error", "Method Not Allowed"));
                return;
            }
            if (!plugin.getConfigManager().isAllowRemoteCommands()) {
                sendResponse(exchange, 403, Map.of("error", "Remote commands are disabled in bridge config.yml"));
                return;
            }

            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Map<?, ?> map = gson.fromJson(body, Map.class);
                String command = map != null && map.get("command") != null ? map.get("command").toString() : null;

                if (command == null || command.isBlank()) {
                    sendResponse(exchange, 400, Map.of("error", "Missing command"));
                    return;
                }

                // Dispatch to Bukkit main thread safely
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                });

                sendResponse(exchange, 200, Map.of("status", "executed", "command", command));
            } catch (Exception e) {
                sendResponse(exchange, 500, Map.of("error", e.getMessage()));
            }
        }));

        server.start();
        plugin.getLogger().info("MSCC-Bridge API listening on http://" + host + ":" + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(1);
            server = null;
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] bytes = gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private class AuthenticatedHandler implements HttpHandler {
        private final HttpHandler delegate;

        public AuthenticatedHandler(HttpHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                token = exchange.getRequestHeaders().getFirst("X-Bridge-Token");
            }

            if (!tokenManager.validateToken(token)) {
                sendResponse(exchange, 401, Map.of("error", "Unauthorized: invalid or missing bridge token"));
                return;
            }

            delegate.handle(exchange);
        }
    }
}
