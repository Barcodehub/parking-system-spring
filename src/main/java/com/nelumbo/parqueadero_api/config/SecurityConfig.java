package com.nelumbo.parqueadero_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelumbo.parqueadero_api.dto.errors.ErrorDetailDTO;
import com.nelumbo.parqueadero_api.dto.errors.ErrorResponseDTO;
import com.nelumbo.parqueadero_api.exception.CustomAccessDeniedException;
import com.nelumbo.parqueadero_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;


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
                                .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/parkings").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/parkings/parkings/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SOCIO")
                                .requestMatchers(HttpMethod.GET, "/api/parkings").hasAnyAuthority("ROLE_ADMIN", "ROLE_SOCIO")
                                .requestMatchers(HttpMethod.GET, "/api/parkings/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/parkings/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/parkings/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/parkings/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/vehicles/entry").hasAnyRole("SOCIO")
                                .requestMatchers(HttpMethod.POST, "/api/vehicles/exit").hasAnyRole("SOCIO")
                                .requestMatchers(HttpMethod.POST, "/api/users/parkings/**").hasAuthority("ROLE_SOCIO")
                                .requestMatchers(HttpMethod.GET, "/api/users/parkings/").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/analityc").hasAnyAuthority("ROLE_ADMIN", "ROLE_SOCIO")
                                .requestMatchers(HttpMethod.GET, "/api/analityc/parkings/top-earnings").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/analityc/socios/top-earnings").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/admin/emails").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/parkings/socio/my-parkings/**").hasAuthority("ROLE_SOCIO")

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