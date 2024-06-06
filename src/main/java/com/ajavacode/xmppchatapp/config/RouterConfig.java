package com.ajavacode.xmppchatapp.config;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class RouterConfig {

    private final Vertx vertx;
    @Bean
    public Router router() {
        Router router = Router.router(vertx);
        router.route()
                .handler(BodyHandler.create(true));
        router.route().handler(CorsHandler.create().allowedMethods(Set.of(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.POST)));
        router.route().handler(StaticHandler.create("static").setCachingEnabled(false));
        return router;
    }

}
