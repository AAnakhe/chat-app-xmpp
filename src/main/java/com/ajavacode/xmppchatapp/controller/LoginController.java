package com.ajavacode.xmppchatapp.controller;

import com.ajavacode.xmppchatapp.utils.ApiResponse;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

import static com.ajavacode.xmppchatapp.constants.EventBusConstants.LOGIN;

@Controller
public class LoginController {

    private final EventBus eventBus;

    public LoginController(Router router, EventBus eventBus) {
        this.eventBus = eventBus;
        router.post("/login")
                .handler(this::handleLogin);

    }

    private void handleLogin(RoutingContext rc) {
        JsonObject payload = rc.body().asJsonObject();
        eventBus.request(LOGIN, payload, message -> {
            if(message.succeeded()) {
                ApiResponse.response(rc, true,200, "successful", message);
            } else {
                ApiResponse.response(rc, false,500, "unsuccessful", message.cause().getMessage());
            }
        });
    }
}
