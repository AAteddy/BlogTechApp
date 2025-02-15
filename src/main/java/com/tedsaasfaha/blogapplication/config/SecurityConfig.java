
package com.tedsaasfaha.blogapplication.config;


import com.tedsaasfaha.blogapplication.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/refresh-token",
                                "/swagger-ui/**",      // Allow Swagger UI resources
                                "/v3/api-docs/**",     // Allow OpenAPI documentation
                                "/swagger-ui.html",    // Allow Swagger UI entry point
                                "/actuator/prometheus" // Allow expose Prometheus metrics
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)) // Set custom entry point
                .sessionManagement(
                        session -> session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
//
//
//


//                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/api/posts").hasAnyRole("WRITER", "ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasAnyRole("WRITER", "ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/api/posts/**").hasAnyRole("READER", "WRITER", "ADMIN")
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/writer/**").hasRole("WRITER")
//                        .requestMatchers("/api/reader/**").hasAnyRole("READER", "WRITER", "ADMIN")