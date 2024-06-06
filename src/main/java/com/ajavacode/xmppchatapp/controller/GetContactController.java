package com.ajavacode.xmppchatapp.controller;

import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.utils.ApiResponse;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

@Controller
public class GetContactController {

    private final EventBus eventBus;

    public GetContactController(Router router, EventBus eventBus) {
        this.eventBus = eventBus;
        router.get("/get_contacts/:username")
                .handler(this::handleGetContacts);
    }

    private void handleGetContacts(RoutingContext rc) {
        String username = rc.request().getParam("username");
        eventBus.request(EventBusConstants.GET_CONTACTS, username, ar -> {
            if (ar.succeeded()) {
                JsonArray contacts = (JsonArray) ar.result().body();
                ApiResponse.response(rc, true, 200, "successful", contacts);
            } else {
                ApiResponse.response(rc, false, 500, "unsuccessful", ar.cause().getMessage());
            }
        });
    }
}
