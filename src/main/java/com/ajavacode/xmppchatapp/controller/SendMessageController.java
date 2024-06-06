package com.ajavacode.xmppchatapp.controller;

import com.ajavacode.xmppchatapp.service.SendGroupMessagesService;
import com.ajavacode.xmppchatapp.utils.WebsocketConnection;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import static com.ajavacode.xmppchatapp.constants.EventBusConstants.INCOMING_MESSAGES;

@Slf4j
@Controller
public class SendMessageController {
    private final EventBus eventBus;
    private final WebsocketConnection websocketConnection;
    private final SendGroupMessagesService groupMessagesService;

    public SendMessageController(Router router, WebsocketConnection websocketConnection, EventBus eventBus, SendGroupMessagesService groupMessagesService) {
        this.websocketConnection = websocketConnection;
        this.eventBus = eventBus;
        this.groupMessagesService = groupMessagesService;
        router.route("/chat/:sender").handler(this::handleSendMessage);
    }

    private void handleSendMessage(RoutingContext rc) {
        String sender = rc.pathParam("sender");
        rc.request().toWebSocket()
                .onSuccess(serverWebSocket -> {
                    serverWebSocket.accept();
                    serverWebSocket.writeTextMessage("websocket connection established");
                    websocketConnection.addWebSocketConnection(sender, serverWebSocket);
                    serverWebSocket.textMessageHandler(message -> {
                        try {
                            String[] path = message.split(":");
                            String receiver = path[0];
                            String content = path[1];
                            JsonObject data = new JsonObject()
                                    .put("sender", sender)
                                    .put("receiver", receiver)
                                    .put("content", content);
                            eventBus.publish(INCOMING_MESSAGES, data);
                        } catch (Exception e) {
                            log.info("Error sending message: ", e);
                        }
                    });
                    serverWebSocket.closeHandler(close -> log.info("WebSocket connection closed"));
                }).onFailure(err -> log.error("WebSocket upgrade failed: {}", err.getMessage()));
    }
}
