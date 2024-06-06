package com.ajavacode.xmppchatapp.handler;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;


import javax.xml.namespace.QName;
import java.io.IOException;


public class ReadReceiptRequest implements ExtensionElement {

    public static final String ELEMENT = "request";
    public static final String NAMESPACE = "urn:xmpp:read-receipts";
    public static final QName QNAME = new QName("urn:xmpp:read-receipts", "request");

    public ReadReceiptRequest() {
    }

    public static ReadReceiptRequest from(Stanza packet) {
        return packet.getExtension(ReadReceiptRequest.class);
    }

    public static String addTo(Message message) {
        message.throwIfNoStanzaId();
        message.addExtension(new ReadReceiptRequest());
        return message.getStanzaId();
    }

    public static void addTo(MessageBuilder messageBuilder) {
        messageBuilder.throwIfNoStanzaId();
        messageBuilder.overrideExtension(new ReadReceiptRequest());
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String toXML(XmlEnvironment enclosingNamespace) {
        return "<request xmlns='" + NAMESPACE + "'/>";
    }

    public static class Provider extends ExtensionElementProvider<ReadReceiptRequest> {
        public Provider() {
        }

        public ReadReceiptRequest parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
            return new ReadReceiptRequest();
        }
    }
}
