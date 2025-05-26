package com.nelumbo.parqueadero_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelumbo.parqueadero_api.dto.errors.ErrorDetailDTO;
import com.nelumbo.parqueadero_api.dto.errors.ErrorResponseDTO;
import com.nelumbo.parqueadero_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static com.nelumbo.parqueadero_api.constants.ApiPaths.*;
import static com.nelumbo.parqueadero_api.constants.Roles.ADMIN;
import static com.nelumbo.parqueadero_api.constants.Roles.SOCIO;


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
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint())
                )
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers(
                                        "/docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()
                                .requestMatchers(HttpMethod.POST, REGISTRO).hasAuthority(ADMIN)
                                .requestMatchers(HttpMethod.POST, PARKINGS).hasAuthority(ADMIN)
                                .requestMatchers(HttpMethod.GET, ALLPARKINGS).hasAnyAuthority(ADMIN, SOCIO)
                                .requestMatchers(HttpMethod.GET, PARKINGS).hasAnyAuthority(ADMIN, SOCIO)
                                .requestMatchers(HttpMethod.GET, ADMINPARKINGS).hasAuthority(ADMIN)
                                .requestMatchers(HttpMethod.POST, ADMINPARKINGS).hasAuthority(ADMIN)
                                .requestMatchers(HttpMethod.PUT, ADMINPARKINGS).hasAuthority(ADMIN)
                                .requestMatchers(HttpMethod.DELETE, ADMINPARKINGS).hasAuthority(ADMIN)
                                .requestMatchers(HttpMethod.GET, SOCIOPARKINGS).hasAuthority(SOCIO)

                                .requestMatchers(HttpMethod.POST, VEHICULE_ENTRY).hasAnyAuthority(SOCIO)
                                .requestMatchers(HttpMethod.POST, VEHICULE_EXIT).hasAnyAuthority(SOCIO)

                                .requestMatchers(HttpMethod.GET, ANALITYC).hasAnyAuthority(ADMIN, SOCIO)
                                .requestMatchers(HttpMethod.GET, EARNINGPARKING).hasAuthority(ADMIN)
                                .requestMatchers(HttpMethod.GET, EARNINGSOCIO).hasAuthority(ADMIN)
                                .requestMatchers(HttpMethod.POST, EMAIL).hasAuthority(ADMIN)

                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManager->
                        sessionManager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();


    }


    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpStatus.FORBIDDEN.value());

            ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                    "403",
                    "No tienes permisos para realizar esta acción",
                    null
            );

            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    null,
                    List.of(errorDetail)
            );

            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        };
    }
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                    "401",
                    "Autenticación requerida",
                    null
            );

            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    null,
                    List.of(errorDetail)
            );

            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        };
    }

}