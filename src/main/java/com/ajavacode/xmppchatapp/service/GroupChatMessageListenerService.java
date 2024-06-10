package com.ajavacode.xmppchatapp.service;

import com.ajavacode.xmppchatapp.utils.WebsocketConnection;
import io.vertx.core.http.ServerWebSocket;
import lombok.RequiredArgsConstructor;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.EntityFullJid;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
@RequiredArgsConstructor
public class GroupChatMessageListenerService {

    private final WebsocketConnection websocketConnection;

    private final Map<String, Boolean> checkListeners = new ConcurrentHashMap<>();

    /*public void processMessage(MultiUserChat muc) {
        String groupJid = muc.getRoom().asEntityBareJidString();

        checkListeners.computeIfAbsent(groupJid, key -> {
            Set<Map.Entry<String, ServerWebSocket>> entries = websocketConnection.getWebSocketConnections().entrySet();
            List<Map.Entry<String, ServerWebSocket>> socket = entries.stream().toList();
            muc.addMessageListener(message -> {
                String sender = message.getFrom().toString().split("/")[1];
                muc.getOccupants().stream().filter(entityFullJid -> !entityFullJid.equals(message.getFrom()))
                        .forEach(entityFullJid -> {
                            String receiver = entityFullJid.toString().split("/")[1];
                            socket.stream().filter(entry -> entry.getKey().equals(receiver))
                                    .forEach(entry -> entry.getValue().writeTextMessage(sender + ":" + message.getBody()));
                        });

            });
            return true;
        });
    }*/

    public void processMessage(MultiUserChat muc) {
        String groupJid = muc.getRoom().asEntityBareJidString();

        checkListeners.computeIfAbsent(groupJid, key -> {
            Map<String, ServerWebSocket> webSocketConnections = websocketConnection.getWebSocketConnections();
            muc.addMessageListener(message -> {
                String sender = message.getFrom().toString().split("/")[1];
                for (EntityFullJid entityFullJid : muc.getOccupants()) {
                    if (!entityFullJid.equals(message.getFrom())) {
                        String receiver = entityFullJid.toString().split("/")[1];
                        ServerWebSocket socket = webSocketConnections.get(receiver);
                        if (socket != null) {
                            socket.writeTextMessage(sender + ":" + message.getBody());
                        }
                    }
                }
            });
            return true;
        });
    }
}
