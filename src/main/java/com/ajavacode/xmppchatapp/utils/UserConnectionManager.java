package com.ajavacode.xmppchatapp.utils;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class UserConnectionManager {
    private final Map<String, AbstractXMPPConnection> abstractXMPPConnections;

    public UserConnectionManager() {
        this.abstractXMPPConnections = new ConcurrentHashMap<>();
    }

    public void addConnection(String username, AbstractXMPPConnection connection) {
        abstractXMPPConnections.put(username, connection);
    }
    public AbstractXMPPConnection getConnection(String username) {
        return abstractXMPPConnections.get(username);
    }

    public Map<String, AbstractXMPPConnection> getConnections() {
        return abstractXMPPConnections;
    }

}
