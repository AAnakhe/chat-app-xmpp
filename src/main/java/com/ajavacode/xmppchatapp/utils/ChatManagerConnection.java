package com.ajavacode.xmppchatapp.utils;

import lombok.Getter;
import org.jivesoftware.smack.chat2.ChatManager;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Configuration
public class ChatManagerConnection {

    private final Map<String, ChatManager> chatManagerConnections;

    public ChatManagerConnection() {
        this.chatManagerConnections = new ConcurrentHashMap<>();
    }

    public ChatManager getChatManagerConnection(String username) {
        return chatManagerConnections.get(username);
    }

    public void addChatManagerConnection(String username, ChatManager connection) {
        chatManagerConnections.put(username, connection);
    }

    public void removeChatManagerConnection(String username) {
        chatManagerConnections.remove(username);
    }

}
