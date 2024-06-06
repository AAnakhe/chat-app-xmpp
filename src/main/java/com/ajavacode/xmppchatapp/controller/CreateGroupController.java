package com.ajavacode.xmppchatapp.controller;

import com.ajavacode.xmppchatapp.utils.ApiResponse;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

import static com.ajavacode.xmppchatapp.constants.EventBusConstants.GROUP_CHAT;

@Controller
public class CreateGroupController {

    private final EventBus eventBus;

    public CreateGroupController(Router router, EventBus eventBus) {
        this.eventBus = eventBus;
        router.post("/create_group")
                .handler(this::handleCreateGroup);
    }

    private void handleCreateGroup(RoutingContext rc) {
        JsonObject payload = rc.body().asJsonObject();
        eventBus.<JsonObject>request(GROUP_CHAT, payload, ar -> {
            if (ar.succeeded()) {
                ApiResponse.response(rc, true, 200, "successful", ar);
            } else {
                ApiResponse.response(rc, false, 500, "unsuccessful", ar.cause().getMessage());
            }
        });
    }
}
