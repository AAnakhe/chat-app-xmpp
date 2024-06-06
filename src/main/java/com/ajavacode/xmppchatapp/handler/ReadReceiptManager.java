package com.ajavacode.xmppchatapp.handler;

import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.MessageWithBodiesFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.Jid;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class ReadReceiptManager extends Manager {
    private static final StanzaFilter NON_ERROR_GROUPCHAT_MESSAGES_WITH_READ_RECEIPT_REQUEST;
    private static final StanzaFilter MESSAGES_WITH_READ_RECEIPT;
    private static final Map<XMPPConnection, ReadReceiptManager> instances;
    private static final StanzaFilter MESSAGES_TO_REQUEST_RECEIPTS_FOR;
    private static ReadReceiptManager.AutoReceiptMode defaultAutoReceiptMode;

    static {
        NON_ERROR_GROUPCHAT_MESSAGES_WITH_READ_RECEIPT_REQUEST = new AndFilter(StanzaTypeFilter.MESSAGE, new StanzaExtensionFilter(new ReadReceiptRequest()), new NotFilter(MessageTypeFilter.ERROR));
        MESSAGES_WITH_READ_RECEIPT = new AndFilter(StanzaTypeFilter.MESSAGE, new StanzaExtensionFilter("received", "urn:xmpp:read-receipts"));
        instances = new WeakHashMap<>();
        XMPPConnectionRegistry.addConnectionCreationListener(ReadReceiptManager::getInstanceFor);
        defaultAutoReceiptMode = ReadReceiptManager.AutoReceiptMode.ifIsSubscribed;
        MESSAGES_TO_REQUEST_RECEIPTS_FOR = new AndFilter(MessageTypeFilter.NORMAL_OR_CHAT_OR_HEADLINE, new NotFilter(new StanzaExtensionFilter("received", "urn:xmpp:read-receipts")), MessageWithBodiesFilter.INSTANCE);
    }

    private final Set<ReceiptReceivedListener> receiptReceivedListeners;
    private ReadReceiptManager.AutoReceiptMode autoReceiptMode;

    private ReadReceiptManager(XMPPConnection connection) {
        super(connection);
        this.autoReceiptMode = defaultAutoReceiptMode;
        this.receiptReceivedListeners = new CopyOnWriteArraySet<>();
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        sdm.addFeature("urn:xmpp:read-receipts");
        connection.addAsyncStanzaListener(packet -> {
            ReadReceipt rr = ReadReceipt.from((Message) packet);

            for (ReceiptReceivedListener l : ReadReceiptManager.this.receiptReceivedListeners) {
                l.onReceiptReceived(packet.getFrom(), packet.getTo(), rr.id(), packet);
            }

        }, MESSAGES_WITH_READ_RECEIPT);
        connection.addAsyncStanzaListener(packet -> {
            Jid from = packet.getFrom();
            XMPPConnection connection1 = ReadReceiptManager.this.connection();
            switch (ReadReceiptManager.this.autoReceiptMode) {
                case disabled:
                    return;
                case ifIsSubscribed:
                    if (!Roster.getInstanceFor(connection1).isSubscribedToMyPresence(from)) {
                        return;
                    }
                case always:
                default:
                    Message messageWithReceiptRequest = (Message) packet;
                    Message ack = ReadReceiptManager.receiptMessageFor(messageWithReceiptRequest);
                    if (ack == null) {
                        ReadReceiptManager.log.warn("Received message stanza with receipt request from '" + from + "' without a stanza ID set. Message: " + messageWithReceiptRequest);
                    } else {
                        connection1.sendStanza(ack);
                    }
            }
        }, NON_ERROR_GROUPCHAT_MESSAGES_WITH_READ_RECEIPT_REQUEST);
    }

    public static void setDefaultAutoReceiptMode(ReadReceiptManager.AutoReceiptMode autoReceiptMode) {
        defaultAutoReceiptMode = autoReceiptMode;
    }

    public static synchronized void getInstanceFor(XMPPConnection connection) {
        ReadReceiptManager receiptManager = instances.get(connection);
        if (receiptManager == null) {
            receiptManager = new ReadReceiptManager(connection);
            instances.put(connection, receiptManager);
        }

    }

    public static boolean hasReadReceiptRequest(Message message) {
        return ReadReceiptRequest.from(message) != null;
    }

    public static Message receiptMessageFor(Message messageWithReceiptRequest) {
        String stanzaId = messageWithReceiptRequest.getStanzaId();
        if (StringUtils.isNullOrEmpty(stanzaId)) {
            return null;
        } else {
            return StanzaBuilder.buildMessage().ofType(messageWithReceiptRequest.getType()).to(messageWithReceiptRequest.getFrom()).addExtension(new ReadReceipt(stanzaId)).build();
        }
    }

    public boolean isSupported(Jid jid) throws SmackException, XMPPException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(this.connection()).supportsFeature(jid, "urn:xmpp:read-receipts");
    }

    public ReadReceiptManager.AutoReceiptMode getAutoReceiptMode() {
        return this.autoReceiptMode;
    }

    public void setAutoReceiptMode(ReadReceiptManager.AutoReceiptMode autoReceiptMode) {
        this.autoReceiptMode = autoReceiptMode;
    }

    public void addReceiptReceivedListener(ReceiptReceivedListener listener) {
        this.receiptReceivedListeners.add(listener);
    }

    public void removeReceiptReceivedListener(ReceiptReceivedListener listener) {
        this.receiptReceivedListeners.remove(listener);
    }

    public void autoAddReadReceiptRequests() {
        this.connection().addMessageInterceptor(ReadReceiptRequest::addTo, MESSAGES_TO_REQUEST_RECEIPTS_FOR::accept);
    }

    public void dontAutoAddDeliveryReceiptRequests() {
        this.connection().removeMessageInterceptor(ReadReceiptRequest::addTo);
    }

    public static enum AutoReceiptMode {
        disabled,
        ifIsSubscribed,
        always;

        private AutoReceiptMode() {
        }
    }

}
