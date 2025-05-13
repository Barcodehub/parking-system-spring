package com.nelumbo.parqueadero_api.config;

import com.nelumbo.parqueadero_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
                .csrf(csrf ->
                        csrf.disable())
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/parkings").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/parkings/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/parkings/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/parkings/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/vehicles/entry").hasAnyRole("SOCIO")
                                .requestMatchers(HttpMethod.POST, "/api/vehicles/exit").hasAnyRole("SOCIO")
                                .requestMatchers(HttpMethod.POST, "/api/users/parkings").hasAuthority("ROLE_SOCIO")
                                .requestMatchers(HttpMethod.GET, "/socio/**").hasAuthority("ROLE_SOCIO")
                                .requestMatchers(HttpMethod.GET, "/api/users/parkings/").hasAuthority("ROLE_ADMIN")


                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManager->
                        sessionManager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();


    }

}