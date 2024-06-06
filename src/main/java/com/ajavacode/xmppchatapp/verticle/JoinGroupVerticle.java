package com.ajavacode.xmppchatapp.verticle;

import com.ajavacode.xmppchatapp.constants.DomainConstant;
import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.service.GroupChatMessageListenerService;
import com.ajavacode.xmppchatapp.utils.UserConnectionManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JoinGroupVerticle extends AbstractVerticle {

    private final UserConnectionManager connectionManager;
    private final GroupChatMessageListenerService chatMessageListener;

    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer(EventBusConstants.JOIN_GROUP, msg -> {
            JsonObject payload = msg.body();
            String username = payload.getString("username");
            String groupName = payload.getString("groupName");

            try {
                AbstractXMPPConnection connection = connectionManager.getConnection(username);
                if (!connection.isConnected()) {
                    connection.connect();
                } else {

                    MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
                    EntityBareJid jid = JidCreate.entityBareFrom(groupName + "@conference." + DomainConstant.DOMAIN);
                    MultiUserChat muc  = multiUserChatManager.getMultiUserChat(jid);
                    muc.join(Resourcepart.fromOrNull(username));
                    chatMessageListener.processMessage(muc);


                    log.info("User {} joined group {}", username, groupName);
                    msg.reply(new JsonObject().put("message","User "+ username + " joined group " + groupName));
                }
            }catch (SmackException | IOException | XMPPException | InterruptedException e) {
            log.error("Unable to join group: {}", e.getMessage());
            msg.fail(500, e.getMessage());
            }
        });
    }
}
