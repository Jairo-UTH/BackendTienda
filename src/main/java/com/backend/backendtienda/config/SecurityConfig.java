package com.backend.backendtienda.config;

import com.backend.backendtienda.service.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                    JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()   

            .requestMatchers("/api/Auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/Category/**", "/api/Product/getAll").permitAll()
            .requestMatchers("/uploads/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/Order/create").permitAll()
            .requestMatchers("/api/Employee/**", "/api/Position/**").hasRole("GERENTE")
            .requestMatchers(HttpMethod.POST, "/api/Product/**").hasRole("GERENTE")
            .requestMatchers(HttpMethod.PUT, "/api/Product/**").hasRole("GERENTE")
            .requestMatchers(HttpMethod.DELETE, "/api/Product/**").hasRole("GERENTE")
            .anyRequest().authenticated()
        )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}