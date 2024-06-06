package com.ajavacode.xmppchatapp.controller;

import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.utils.ApiResponse;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

@Controller
public class CreateAccountController {

    private final EventBus eventBus;

    public CreateAccountController(Router router, EventBus eventBus) {
        this.eventBus = eventBus;
        router.post("/create_account")
                .handler(this::handleCreateAccount);
    }

    private void handleCreateAccount(RoutingContext rc) {

        JsonObject payload = rc.body().asJsonObject();

        eventBus.<JsonObject>request(EventBusConstants.CREATE_ACCOUNT, payload, msg -> {
            if (msg.succeeded()) {
                ApiResponse.response(rc, true, 200, "successful", msg);
            } else {
                ApiResponse.response(rc, false, 500, "unsuccessful", msg.cause().getMessage());
            }
        });
    }
}
