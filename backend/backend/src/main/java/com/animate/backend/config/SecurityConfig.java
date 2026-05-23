package com.animate.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults()) // Habilita o CORS com a configuração padrão
            .csrf(csrf -> csrf.disable()) // Desabilita o CSRF
            .authorizeHttpRequests(authorize -> authorize
                // Rotas públicas (sem autenticação)
                .requestMatchers("/auth/signup", "/auth/signin", "/auth/forgot-password", "/auth/reset-password").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                
                // Rotas protegidas (requerem autenticação)
                .requestMatchers("/auth/check", "/auth/signout").authenticated()
                .requestMatchers("/profile/**").authenticated()
                
                // Qualquer outra requisição requer autenticação
                .anyRequest().authenticated()
            )
            // Adiciona o filtro JWT antes do filtro de autenticação padrão
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
