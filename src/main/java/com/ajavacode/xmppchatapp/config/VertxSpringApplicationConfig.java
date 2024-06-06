package com.ajavacode.xmppchatapp.config;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class VertxSpringApplicationConfig implements ApplicationContextAware, VerticleFactory {

    private ApplicationContext applicationContext;

    @Override
    public String prefix() {
        return "XMPP";
    }

    @Override
    public void createVerticle(String s, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {

        String verticleName = VerticleFactory.removePrefix(s);
        promise.complete(()-> (Verticle) applicationContext.getBean(Class.forName(verticleName)));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}