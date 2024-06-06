package com.ajavacode.xmppchatapp.config;

import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VertxShutdown implements DisposableBean {

    public final Vertx vertx;

    @Override
    public void destroy() throws Exception {
        vertx.close();
    }
}
