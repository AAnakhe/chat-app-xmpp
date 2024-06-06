package com.ajavacode.xmppchatapp.handler;

import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jxmpp.jid.EntityBareJid;

@Slf4j
public class ReceiptHandler {

    private final AbstractXMPPConnection connection;

    public ReceiptHandler(AbstractXMPPConnection connection) {
        this.connection = connection;
    }

    public void handleReceipt(ExtensionElement extension, EntityBareJid from, Message message) {
        try {
            Message ack = MessageBuilder.buildMessage()
                    .ofType(Message.Type.normal)
                    .to(from)
                    .build();

            if (extension.getNamespace().equals(DeliveryReceipt.NAMESPACE)) {
                ack.addExtension(new DeliveryReceipt(message.getStanzaId()));
                log.info("Delivery receipt sent for message ID: {}", message.getStanzaId());
            } else if (extension.getNamespace().equals(ReadReceipt.NAMESPACE)) {
                ack.addExtension(new ReadReceipt(message.getStanzaId()));
                log.info("Read receipt sent for message ID: {}", message.getStanzaId());
            } else {
                log.warn("Ignoring unexpected extension: {}", extension.getElementName());
                return;
            }

            connection.sendStanza(ack);
        } catch (Exception e) {
            if (extension.getNamespace().equals(DeliveryReceipt.NAMESPACE)) {
                log.error("Error sending delivery receipt: {}", e.getMessage(), e);
            } else {
                log.error("Error sending read receipt: {}", e.getMessage(), e);
            }
        }
    }
}

