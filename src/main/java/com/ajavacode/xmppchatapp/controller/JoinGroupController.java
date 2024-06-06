package com.ajavacode.xmppchatapp.controller;

import com.ajavacode.xmppchatapp.constants.EventBusConstants;
import com.ajavacode.xmppchatapp.utils.ApiResponse;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

@Controller
public class JoinGroupController {

    private final EventBus eventBus;

    public JoinGroupController(Router router, EventBus eventBus) {
        this.eventBus = eventBus;
        router.post("/join_group/:username/:groupName")
                .handler(this::handleJoinGroup);
    }

    private void handleJoinGroup(RoutingContext rc) {
        String username = rc.request().getParam("username");
        String groupName = rc.request().getParam("groupName");
        JsonObject obj = new JsonObject();
        obj.put("username", username);
        obj.put("groupName", groupName);

        eventBus.<JsonObject>request(EventBusConstants.JOIN_GROUP, obj, ar -> {
            if (ar.succeeded()){
                ApiResponse.response(rc,true, 200, "successful", ar);
            } else {
               ApiResponse.response(rc,false, 500, "unsuccessful", ar.cause().getMessage());
            }
        });
    }
}
