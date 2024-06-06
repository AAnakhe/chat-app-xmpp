package com.ajavacode.xmppchatapp.config;

import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.c2s.ModularXmppClientToServerConnectionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class XmppConfig {

    @Bean
    public ModularXmppClientToServerConnectionConfiguration connectXmppServer() {
        try {
            log.info("XMPP server connected.");
            return ModularXmppClientToServerConnectionConfiguration.builder()
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setPort(5222)
                    .setHost("127.0.0.1")
                    .setSendPresence(true)
                    .setXmppDomain("hp-pavilion-gaming-laptop")
                    .build();
        } catch (IOException e) {
            log.error("unable to connect to XMPP server {}", e.getCause().getMessage());
            throw new RuntimeException("unable to connect to XMPP server " + e.getCause().getMessage());
        }
    }
}
