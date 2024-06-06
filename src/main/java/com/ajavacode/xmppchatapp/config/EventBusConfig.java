package com.ajavacode.xmppchatapp.config;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {

    @Bean
    @Autowired
    EventBus eventBus(Vertx vertx){
        return vertx.eventBus();
    }
}
