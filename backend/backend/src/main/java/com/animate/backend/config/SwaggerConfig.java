package com.animate.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AniMate API")
                        .version("1.0")
                        .description("API do projeto AniMate — plataforma de animação em equipe")
                        .contact(new Contact()
                                .name("Time AniMate")));
    }
}