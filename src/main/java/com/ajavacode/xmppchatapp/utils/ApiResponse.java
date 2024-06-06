package com.ajavacode.xmppchatapp.utils;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ApiResponse {
    public static void response(RoutingContext rc, Boolean status, int statusCode, String message, Object data) {
      var response = new JsonObject().put("status", status)
                .put("message", message)
                .put("data", data).encode();
      rc.response().putHeader("content-type", "application/json")
              .setStatusCode(statusCode)
              .end(response);
    }
}
