package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.config.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

/**
 * Spring Security Configuration
 * JWT-based authentication with stateless sessions
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.addAllowedOrigin("http://localhost:5173"); // Vue dev server (main frontend)
                    corsConfig.addAllowedMethod("*"); // Allow all HTTP methods
                    corsConfig.addAllowedHeader("*"); // Allow all headers
                    corsConfig.setAllowCredentials(true); // Allow cookies/auth headers
                    corsConfig.setMaxAge(3600L); // Cache preflight for 1 hour
                    return corsConfig;
                }))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health")
                        .permitAll() // Public endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin endpoints
                        .requestMatchers("/api/users/**").hasRole("ADMIN") // User management
                        .requestMatchers("/api/products/**").hasRole("ADMIN") // Product management
                        .requestMatchers(HttpMethod.POST, "/api/companies/**").hasRole("ADMIN") // Company creation
                        .requestMatchers(HttpMethod.GET, "/api/orders", "/api/orders/*/quote", "/api/orders/*/status")
                        .hasRole("ADMIN") // Admin order views
                        .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasRole("ADMIN") // Admin order updates
                        .requestMatchers("/api/orders/my", "/api/orders/upload").authenticated() // Customer order
                                                                                                 // access
                        .anyRequest().authenticated()) // All other requests require authentication
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
