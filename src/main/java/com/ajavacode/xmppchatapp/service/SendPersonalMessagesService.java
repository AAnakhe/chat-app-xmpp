package com.ajavacode.xmppchatapp.service;

import com.ajavacode.xmppchatapp.constants.DomainConstant;
import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.handler.ReadReceiptRequest;
import com.ajavacode.xmppchatapp.utils.ChatManagerConnection;
import com.ajavacode.xmppchatapp.utils.UserConnectionManager;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendPersonalMessagesService {

    private final ChatManagerConnection chatManagerConnection;
    private final UserConnectionManager userConnectionManager;

    public SendPersonalMessagesService(EventBus eventBus, ChatManagerConnection chatManagerConnection, UserConnectionManager userConnectionManager) {
        this.chatManagerConnection = chatManagerConnection;
        this.userConnectionManager = userConnectionManager;
        eventBus.<JsonObject>consumer(EventBusConstants.INCOMING_MESSAGES, msg -> {
            var obj = msg.body();
            String sender = obj.getString("sender");
            String receiver = obj.getString("receiver");
            String content = obj.getString("content");
            sendMessage(sender, receiver, content);
        });
    }

    public void sendMessage(String sender, String receiver, String content) {
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(receiver + "@" + DomainConstant.DOMAIN);
            ChatManager chatManager = chatManagerConnection.getChatManagerConnection(sender);
            AbstractXMPPConnection connection = userConnectionManager.getConnection(sender);
            Message message1 = connection.getStanzaFactory().buildMessageStanza()
                    .to(jid)
                    .setBody(content)
                    .ofType(Message.Type.chat)
                    .addExtension(new DeliveryReceiptRequest())
                    .addExtension(new ReadReceiptRequest())
                    .build();
            if (chatManager != null) {
                connection.sendStanza(message1);
            }
        } catch (Exception e) {
            log.error("Failed to send message: {}", e.getMessage());
            throw new RuntimeException("Error occurred", e);
        }
    }
}