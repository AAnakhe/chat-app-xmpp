package com.ajavacode.xmppchatapp.verticle;

import io.vertx.core.AbstractVerticle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainVerticle extends AbstractVerticle {

    private final LoginVerticle loginVerticle;
    private final LogoutVerticle logoutVerticle;
    private final JoinGroupVerticle joinGroupVerticle;
    private final AddContactVerticle addContactVerticle;
    private final GetContactsVerticle getContactsVerticle;
    private final CreateGroupVerticle createGroupVerticle;
    private final CreateAccountVerticle createAccountVerticle;

    public void start() throws Exception {
        vertx.deployVerticle(loginVerticle);
        vertx.deployVerticle(logoutVerticle);
        vertx.deployVerticle(joinGroupVerticle);
        vertx.deployVerticle(addContactVerticle);
        vertx.deployVerticle(createGroupVerticle);
        vertx.deployVerticle(getContactsVerticle);
        vertx.deployVerticle(createAccountVerticle);
    }
}
