package com.ajavacode.xmppchatapp.verticle;

import com.ajavacode.xmppchatapp.config.XmppConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.c2s.ModularXmppClientToServerConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.parts.Localpart;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.ajavacode.xmppchatapp.constants.EventBusConstants.CREATE_ACCOUNT;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateAccountVerticle extends AbstractVerticle {

    private final XmppConfig xmppConfig;

    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer(CREATE_ACCOUNT, msg -> {
            ModularXmppClientToServerConnection connection = new ModularXmppClientToServerConnection(xmppConfig.connectXmppServer());
            JsonObject msgBody = msg.body();
            String username = msgBody.getString("username");
            String password = msgBody.getString("password");

            AccountManager accountManager = AccountManager.getInstance(connection);
            if(!connection.isConnected()){
            try {
                connection.connect();
                accountManager.sensitiveOperationOverInsecureConnection(true);
                    accountManager.createAccount(Localpart.fromOrThrowUnchecked(username), password);
                log.info("Account created successfully for user: {}", username);
                    msg.reply(new JsonObject().put("status", username));
                } catch (InterruptedException | SmackException | XMPPException | IOException e) {
                    msg.fail(500, "error occurred: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }else {
                log.info("Client is not connected");
                msg.fail(500, "Client is not connected");
            }
        });
    }
}
