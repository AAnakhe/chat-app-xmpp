package com.ajavacode.xmppchatapp.service;

import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.handler.ReadReceiptRequest;
import com.ajavacode.xmppchatapp.handler.ReceiptHandler;
import com.ajavacode.xmppchatapp.utils.ChatManagerConnection;
import com.ajavacode.xmppchatapp.utils.UserConnectionManager;
import com.ajavacode.xmppchatapp.utils.WebsocketConnection;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersonalMessageListenerService {
    private final ChatManagerConnection chatManagerConnection;
    private final UserConnectionManager userConnectionManager;
    private final WebsocketConnection websocketConnection;

    public PersonalMessageListenerService(EventBus eventBus, ChatManagerConnection chatManagerConnection, UserConnectionManager userConnectionManager, WebsocketConnection websocketConnection) {
        this.chatManagerConnection = chatManagerConnection;
        this.userConnectionManager = userConnectionManager;
        this.websocketConnection = websocketConnection;
        eventBus.<String>consumer(EventBusConstants.SENDER, msg -> incomingMessage(msg.body()));
    }

    public void incomingMessage(String sender) {
        AbstractXMPPConnection connection = userConnectionManager.getConnection(sender);
        ChatManager chatManager = chatManagerConnection.getChatManagerConnection(sender);

        chatManager.addIncomingListener((entityBareJid, message, chat) -> {
            if (websocketConnection.getWebSocketConnection(sender) != null) {
                String[] split = entityBareJid.asEntityBareJidString().split("@");
                websocketConnection.getWebSocketConnection(sender).writeTextMessage(split[0] + ":" + message.getBody());
                log.info("MessageListener: Incoming message.... to {} from {}", sender, message.getFrom());
            }

            try {
                EntityBareJid from = JidCreate.entityBareFrom(entityBareJid);
                ReceiptHandler receiptHandler = new ReceiptHandler(connection);

                message.getExtensions().forEach(extension -> {
                    if (extension instanceof DeliveryReceiptRequest || extension instanceof ReadReceiptRequest) {
                        receiptHandler.handleReceipt(extension, from, message);
                    }
                });
            } catch (XmppStringprepException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

