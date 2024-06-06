package com.ajavacode.xmppchatapp.verticle;

import com.ajavacode.xmppchatapp.constants.DomainConstant;
import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.utils.UserConnectionManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddContactVerticle extends AbstractVerticle {
    private AbstractXMPPConnection connection;
    private final UserConnectionManager userConnectionManager;

    @Override
    public void start() throws Exception {

        vertx.eventBus().<JsonObject>consumer(EventBusConstants.ADD_CONTACT, message -> {
            Roster roster;
            JsonObject event = message.body();
            String username = event.getString("pathParam");
            String contact = event.getString("contact");
            try {
                if (userConnectionManager.getConnection(username) != null) {
                    connection = userConnectionManager.getConnection(username);
                    roster = Roster.getInstanceFor(connection);
                    BareJid contactJid = JidCreate.bareFrom(contact + "@" + DomainConstant.DOMAIN);
                    int beforeCount = roster.getEntries().size();
                    roster.createItem(contactJid, contact, null);
                    int afterCount = roster.getEntries().size();

                    if (afterCount > beforeCount) {
                        message.reply(new JsonObject().put("message", "Contact" + contact + " added successfully to user " + connection.getUser()));
                    } else {
                        message.fail(401, "User " + contact + " already exist in your contact list ");
                    }
                } else {
                    message.fail(500, "User not connected.");
                }
            } catch (XMPPException.XMPPErrorException | SmackException.NotConnectedException |
                     SmackException.NoResponseException | SmackException.NotLoggedInException | InterruptedException |
                     XmppStringprepException e) {
                message.fail(401, "Error: " + e.getMessage());
            }
        });
    }
}
