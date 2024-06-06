package com.ajavacode.xmppchatapp.service;

import com.ajavacode.xmppchatapp.constants.DomainConstant;
import com.ajavacode.xmppchatapp.utils.UserConnectionManager;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendGroupMessagesService {

    private final UserConnectionManager connectionManager;

    public void chat(JsonObject data) {
        String username = data.getString("username");
        String groupName = data.getString("groupName");
        String text = data.getString("text");

        try {
            AbstractXMPPConnection connection = connectionManager.getConnection(username);
            if (!connection.isConnected()) {
                connection.connect();
            } else {

                MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
                EntityBareJid jid = JidCreate.entityBareFrom(groupName + "@conference." + DomainConstant.DOMAIN);
                MultiUserChat muc  = multiUserChatManager.getMultiUserChat(jid);
                MessageBuilder messageBuilder = MessageBuilder.buildMessage()
                                .ofType(Message.Type.groupchat)
                                        .setBody(text)
                                                .to(jid);
                muc.sendMessage(messageBuilder);
            }
        }catch (SmackException | IOException | XMPPException | InterruptedException e) {
            log.error("Unable to send message: {}", e.getMessage());
            throw new RuntimeException("Unable to send message: ", e);
        }

    }
}
