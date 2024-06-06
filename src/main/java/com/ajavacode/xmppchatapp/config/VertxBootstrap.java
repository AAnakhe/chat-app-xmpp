package com.ajavacode.xmppchatapp.config;

import com.ajavacode.xmppchatapp.verticle.MainVerticle;
import com.ajavacode.xmppchatapp.verticle.WebServerVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VertxBootstrap implements CommandLineRunner {

    private final Vertx vertx;
    private final WebServerVerticle webServerVerticle;
    private final MainVerticle mainVerticle;

    @Override
    public void run(String... args) throws Exception {
        Future<String> deployServerVerticle = Future.future(promise -> vertx.deployVerticle(webServerVerticle));
        Future<String> deployMainVerticle= Future.future(promise -> vertx.deployVerticle(mainVerticle));
        Future.all(deployServerVerticle, deployMainVerticle).onComplete(compositeFuture -> {
            if (compositeFuture.succeeded()){
                log.info("Successfully deployed verticles");
            }else {
                log.error("error " + compositeFuture.cause().getMessage());
            }
        });
    }
}

