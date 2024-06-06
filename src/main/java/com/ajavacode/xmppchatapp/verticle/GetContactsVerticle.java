package com.ajavacode.xmppchatapp.verticle;

import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.utils.UserConnectionManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetContactsVerticle extends AbstractVerticle {

    private final UserConnectionManager userConnectionManager;

    @Override
    public void start() throws Exception {
        vertx.eventBus().<String>consumer(EventBusConstants.GET_CONTACTS, message -> {
            String username = message.body();
            try {
                AbstractXMPPConnection connection = userConnectionManager.getConnection(username);
                if (connection != null) {
                    List<String> contacts = new ArrayList<>();
                    Roster roster = Roster.getInstanceFor(connection);
                    for (RosterEntry entry: roster.getEntries()) {
                        contacts.add(entry.getName());
                    }
                    JsonArray jsonArray = new JsonArray(contacts);
                    log.info("contact list: {}", contacts);
                    message.reply(jsonArray);
                } else {
                    message.fail(401, "User not connected.");
                }
            } catch (Exception e) {
                message.fail(500, "Error: " + e.getMessage());
            }
        });
    }
}
