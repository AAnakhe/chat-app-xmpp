package com.ajavacode.xmppchatapp.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.web.Router;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebServerVerticle extends AbstractVerticle {


    private final Router router;
    @Value("${abaaServerConfig.port}")
    private int port;
    @Value("${abaaServerConfig.host}")
    private String host;

    @Override
    public void start() throws Exception {
        HttpServerOptions serverOptions = new HttpServerOptions()
                .setRegisterWebSocketWriteHandlers(true);
        serverOptions.setTracingPolicy(TracingPolicy.ALWAYS);
        serverOptions.setPort(port);
        serverOptions.setHost(host);

        vertx.createHttpServer(serverOptions)
                .requestHandler(router)
                .listen(port, host)
                .onComplete(event -> {
                    if (event.succeeded()) {
                        log.info("Http Server is Running on Port: {}", event.result().actualPort());
                    } else {
                        log.error("StarServerError", event.cause());
                    }
                });
    }
}

