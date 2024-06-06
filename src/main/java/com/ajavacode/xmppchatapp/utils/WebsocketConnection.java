package com.ajavacode.xmppchatapp.utils;

import io.vertx.core.http.ServerWebSocket;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class WebsocketConnection {
    private final Map<String, ServerWebSocket> webSocketConnections;

    public WebsocketConnection() {
        this.webSocketConnections = new ConcurrentHashMap<>();
    }

    public Map<String, ServerWebSocket> getWebSocketConnections() {
        return webSocketConnections;
    }

    public ServerWebSocket getWebSocketConnection(String username) {
        return webSocketConnections.get(username);
    }

    public void addWebSocketConnection(String username, ServerWebSocket connection) {
        webSocketConnections.put(username, connection);
    }

    public void removeWebSocketConnection(String username) {
        webSocketConnections.remove(username);
    }

}
