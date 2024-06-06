package com.ajavacode.xmppchatapp.controller;

import com.ajavacode.xmppchatapp.utils.ApiResponse;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import static com.ajavacode.xmppchatapp.constants.EventBusConstants.ADD_CONTACT;

@Slf4j
@Controller
public class AddContactController {

    private final EventBus eventBus;

    public AddContactController(Router router, EventBus eventBus) {
        this.eventBus = eventBus;
        router.post("/add_contact/:username")
                .handler(this::handleAddContact);
    }

    private void handleAddContact(RoutingContext rc) {
        String username = rc.request().getParam("username");
        JsonObject jsonObject = rc.body().asJsonObject();
        jsonObject.put("pathParam", username);

        eventBus.<JsonObject>request(ADD_CONTACT, jsonObject, ar -> {
            var contact = jsonObject.getString("contact");
            if (ar.succeeded()){
                log.info("successful added {} to contact list", contact);
                ApiResponse.response(rc, true, 200, "successful", ar);
            } else {
                log.error("error occurred while adding user to contact list {}", ar.cause().getMessage());
                ApiResponse.response(rc, false, 500, "unsuccessful", ar.cause().getMessage());
            }
        });
    }
}
