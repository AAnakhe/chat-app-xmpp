package com.ajavacode.xmppchatapp.handler;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

public record ReadReceipt(String id) implements ExtensionElement {

    public static final String NAMESPACE = "urn:xmpp:read-receipts";
    public static final String ELEMENT = "received";
    public static final QName QNAME = new QName("urn:xmpp:read-receipts", "received");

    public static ReadReceipt from(Message message) {
        return message.getExtension(ReadReceipt.class);
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(XmlEnvironment enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.optAttribute("id", this.id);
        xml.closeEmptyElement();
        return xml;
    }

    public static class Provider extends EmbeddedExtensionProvider<ReadReceipt> {
        public Provider() {
        }

        protected ReadReceipt createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
            return new ReadReceipt(attributeMap.get("id"));
        }
    }
}
