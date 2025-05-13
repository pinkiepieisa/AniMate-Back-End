package com.animate.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) { // Configura o agente das mensagens
        registry.enableSimpleBroker("/canvas"); // Agente das mensagens que leva as mensagens ao cliente
        registry.setApplicationDestinationPrefixes("/app"); // Filtra os destinos direcionados aos métodos anotados do aplicativo
        //
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { //Registra os endpoints: dipositivos físicos ou virtuais que podem enviar e receber informações
        registry.addEndpoint("projectchat");
        registry.addEndpoint("projectcanvas");
    }
}
