package com.ajavacode.xmppchatapp.controller;

import com.ajavacode.xmppchatapp.utils.ApiResponse;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

import static com.ajavacode.xmppchatapp.constants.EventBusConstants.LOGOUT;

@Controller
public class LogoutController {
    private final EventBus eventBus;

    public LogoutController(Router router, EventBus eventBus) {
        this.eventBus = eventBus;
        router.post("/logout/:username")
                .handler(this::handleLogout);

    }

    private void handleLogout(RoutingContext rc) {
        String username = rc.request().getParam("username");
        eventBus.<String>request(LOGOUT, username, ar -> {
            if (ar.succeeded()) {
                ApiResponse.response(rc, true, 200, "successful", ar);
            } else {
                ApiResponse.response(rc, false, 500, "unsuccessful", ar.cause().getMessage());
            }
        });
    }
}
