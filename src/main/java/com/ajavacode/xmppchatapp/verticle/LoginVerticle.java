package com.ajavacode.xmppchatapp.verticle;

import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.utils.ChatManagerConnection;
import com.ajavacode.xmppchatapp.utils.UserConnectionManager;
import com.ajavacode.xmppchatapp.config.XmppConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.c2s.ModularXmppClientToServerConnection;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginVerticle extends AbstractVerticle {

    private final XmppConfig xmppConfig;
    private final UserConnectionManager userConnectionManager;
    private final ChatManagerConnection chatManagerConnection;

    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer(EventBusConstants.LOGIN, message -> {
            JsonObject credentials = message.body();
            String username = credentials.getString("username");
            String password = credentials.getString("password");

            AbstractXMPPConnection connection = new ModularXmppClientToServerConnection(xmppConfig.connectXmppServer());
            try {
                if (userConnectionManager.getConnections().containsKey(username)) {
                    message.fail(401, "Failure User already logged in.");
                } else {
                    connection.connect().login(username, password);
                    Presence presence = PresenceBuilder.buildPresence()
                            .ofType(Presence.Type.available)
                            .setStatus("online")
                            .build();
                    connection.sendStanza(presence);
                    userConnectionManager.addConnection(username, connection);
                    chatManagerConnection.addChatManagerConnection(username, ChatManager.getInstanceFor(connection));
                    log.info("User {} successfully logged in ", username);
                    message.reply(new JsonObject().put("User {} successful log in", username));
                    vertx.eventBus().publish(EventBusConstants.SENDER, username);
                }
            } catch (Exception e) {
                message.fail(500, e.getMessage());
                log.info("Failure to log in User {} because {}.", username, e.getMessage());
            }
        });
    }
}
