package com.ajavacode.xmppchatapp.verticle;

import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.utils.ChatManagerConnection;
import com.ajavacode.xmppchatapp.utils.UserConnectionManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutVerticle extends AbstractVerticle {
    private final UserConnectionManager userConnections;
    private final ChatManagerConnection chatManagerConnection;
    private AbstractXMPPConnection connection;

    @Override
    public void start() throws Exception {
        vertx.eventBus().<String>consumer(EventBusConstants.LOGOUT, message -> {
            String username = message.body();

            if (!userConnections.getConnections().containsKey(username)) {
                message.fail(404, "user not logged in.");
                log.info("User {} is not logged in.", username);
                return;
            }
            connection = userConnections.getConnection(username);
            try {
                if (connection.isConnected() && connection.isAuthenticated()) {
                    connection.disconnect();
                    userConnections.getConnections().remove(username);
                    chatManagerConnection.removeChatManagerConnection(username);
                    log.info("User {} logged out successfully.", username);
                    message.reply(new JsonObject().put("message", "User logged out successfully"));
                } else {
                    log.info("User {} is already disconnected or not authenticated.", username);
                    message.reply(new JsonObject().put("message", "User already disconnected or not authenticated."));
                }
            } catch (Exception e) {
                message.fail(401, "Failed to logged out User" + e);
                log.error("Failed to logged out User {}:  ", username, e);
                throw new RuntimeException(e.getMessage());
            }
        });


    }
}
