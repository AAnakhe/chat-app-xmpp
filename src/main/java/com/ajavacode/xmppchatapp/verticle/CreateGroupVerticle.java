package com.ajavacode.xmppchatapp.verticle;

import com.ajavacode.xmppchatapp.constants.DomainConstant;
import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.utils.UserConnectionManager;
import com.ajavacode.xmppchatapp.config.XmppConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateGroupVerticle extends AbstractVerticle {

    private final XmppConfig xmppConfig;
    private final UserConnectionManager userConnectionManager;

    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer(EventBusConstants.GROUP_CHAT, msg -> {
            try {
                JsonObject obj = msg.body();
                String ownerJid = obj.getString("ownerJid");
                String groupName = obj.getString("groupName");
                String description = obj.getString("description");
                AbstractXMPPConnection connection = userConnectionManager.getConnection(ownerJid);

                if (connection == null){
                    msg.fail(500, "connection is null, user not logged in");
                } else if (!connection.isConnected()) {
                    connection.connect();
                } else {
                    MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
                    EntityBareJid groupJid = JidCreate.entityBareFrom(Localpart.from(groupName) + "@conference." + DomainConstant.DOMAIN);
                    Set<Jid> owner = new HashSet<>();
                    owner.add(JidCreate.entityBareFrom(ownerJid + "@" + DomainConstant.DOMAIN));
                    MultiUserChat muc = multiUserChatManager.getMultiUserChat(groupJid);
                    muc.createOrJoin(Resourcepart.fromOrNull(ownerJid))
                            .getConfigFormManager()
                            .setRoomOwners(owner)
                            .submitConfigurationForm();

                    FillableForm form = muc.getConfigurationForm().getFillableForm();
                    /*form.setAnswer("muc#roomconfig_moderatedroom", "1");*/
                    form.setAnswer("muc#roomconfig_roomdesc", description);
                    form.setAnswer("muc#roomconfig_persistentroom", true);
                    muc.sendConfigurationForm(form);

                    log.info("{} group successfully created", groupName);
                    msg.reply(new JsonObject().put("status", groupName + " Group created"));

                }
            } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                log.error(e.getMessage(), e);
                msg.fail(500, e.getMessage());
            }
        });
    }
}
