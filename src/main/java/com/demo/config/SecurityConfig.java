package com.demo.config;

import com.demo.entities.enums.Role;
import com.demo.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final int SALT_ROUNDS = 12;

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(SALT_ROUNDS);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http.csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request ->
                        request
                                //--------------------- Auth ----------------------------
                                .requestMatchers("/api/v1/auth/**").permitAll()

                                //--------------------- Users ----------------------------
                                .requestMatchers("/api/v1/users/*/role").hasAuthority(Role.ROLE_SUPERADMIN.name())
                                .requestMatchers("/api/v1/users/profile").authenticated()
                                .requestMatchers("/api/v1/users/**").hasAnyAuthority(Role.ROLE_SUPERADMIN.name(), Role.ROLE_ADMIN.name())

                                //--------------------- Rooms ----------------------------
                                .requestMatchers(HttpMethod.GET,"/api/v1/rooms/**").authenticated()
                                .requestMatchers("/api/v1/rooms/**").hasAnyAuthority(Role.ROLE_SUPERADMIN.name(), Role.ROLE_ADMIN.name())

                                //--------------------- Reservations ----------------------------
                                .requestMatchers(HttpMethod.POST,"/api/v1/reservations").hasAnyAuthority(Role.ROLE_USER.name())
                                .requestMatchers("/api/v1/reservations/*/resume").hasAnyAuthority(Role.ROLE_USER.name())
                                .requestMatchers(HttpMethod.DELETE,"/api/v1/reservations/{id}").hasAnyAuthority(Role.ROLE_USER.name())
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/reservations/{id}/resume").hasAnyAuthority(Role.ROLE_USER.name())
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/reservations/{id}/accept").hasAnyAuthority(Role.ROLE_SUPERADMIN.name(), Role.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/reservations/{id}/reject").hasAnyAuthority(Role.ROLE_SUPERADMIN.name(), Role.ROLE_ADMIN.name())
                                .requestMatchers("/api/v1/reservations").authenticated()

                                //--------------------- Others ----------------------------
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html"
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
