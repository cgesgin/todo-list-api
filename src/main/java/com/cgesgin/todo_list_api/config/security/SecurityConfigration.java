package com.cgesgin.todo_list_api.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigration {

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private UserDetailsService userDetailsService;

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

                httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                                .authorizeHttpRequests(
                                                requests -> requests.requestMatchers("/api/v1/auth/login").permitAll()
                                                                .requestMatchers("api/v1/auth/register").permitAll()
                                                                .requestMatchers("api/v1/auth/refresh-token").permitAll()
                                                                .requestMatchers("/h2-console/**").permitAll()
                                                                .requestMatchers("/api/swagger-ui/**", "/v3/api-docs*/**","/api/doc").permitAll()
                                                                .anyRequest()
                                                                .authenticated())
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                                .addFilter(new JwtAuthenticationFilter(
                                                authenticationManager(httpSecurity
                                                                .getSharedObject(AuthenticationConfiguration.class)),
                                                jwtUtil))
                                .addFilter(new JwtAuthorizationFilter(
                                                authenticationManager(httpSecurity
                                                                .getSharedObject(AuthenticationConfiguration.class)),
                                                jwtUtil,
                                                userDetailsService));
                return httpSecurity.build();
        }

}