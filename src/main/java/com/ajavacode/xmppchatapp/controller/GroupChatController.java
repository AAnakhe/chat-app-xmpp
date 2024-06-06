package com.ajavacode.xmppchatapp.controller;

import com.ajavacode.xmppchatapp.service.SendGroupMessagesService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class GroupChatController {

    private final SendGroupMessagesService service;

    public GroupChatController(Router router, SendGroupMessagesService service) {
        this.service = service;
        router.route("/group/:sender").handler(this::handleGroupChat);
    }

    private void handleGroupChat(RoutingContext rc) {
        String sender = rc.request().getParam("sender");

        rc.request().toWebSocket()
                .onSuccess(serverWebSocket -> {
                    serverWebSocket.accept();
                    serverWebSocket.writeTextMessage("websocket connection established");
                    serverWebSocket.textMessageHandler(message -> {
                        try {
                            String[] split = message.split(":");
                            String groupName = split[0];
                            String text = split[1];
                            JsonObject data = new JsonObject()
                                    .put("sender", sender)
                                    .put("groupName", groupName)
                                    .put("text", text);
                            service.chat(data);
                        }catch (Exception e) {
                            log.info("Error sending message: ", e);
                        }
                    });
                    serverWebSocket.closeHandler(close -> System.out.println("WebSocket connection closed"));
                }).onFailure(err -> System.out.println("WebSocket upgrade failed: " + err.getMessage()));

    }
}
