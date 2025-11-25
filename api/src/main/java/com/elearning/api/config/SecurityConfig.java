package com.elearning.api.config;

import com.elearning.common.components.security.CustomJwtAuthConverter;
import com.elearning.common.components.security.handler.AccessDeniedHandler;
import com.elearning.common.components.security.handler.UnauthorizedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UnauthorizedHandler unauthorizedHandler;
    private final AccessDeniedHandler accessDeniedHandler;
    private final CustomJwtAuthConverter customJwtAuthConverter;
//    private final ApiTransactionFilter apiTransactionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(configurer ->
                        configurer.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                )
                .authorizeHttpRequests(authorize -> authorize
                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Public endpoints
                        .requestMatchers("/api/wba/v1/auth/**", "/api/wba/v1/public/**", "/api/v1/image/**", "/api/wba/v1/image/**").permitAll()

                        // Role management endpoints - accessible to authenticated users
                        .requestMatchers(HttpMethod.GET, "/api/wba/v1/roles").authenticated()

                        // USER MANAGEMENT
                        .requestMatchers(HttpMethod.POST, "/api/wba/v1/users").hasAnyAuthority("USER:CREATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/wba/v1/users").hasAnyAuthority("USER:DELETE")
                        .requestMatchers(HttpMethod.PUT, "/api/wba/v1/users/{userId}").hasAnyAuthority("USER:UPDATE")
                        .requestMatchers(HttpMethod.PATCH, "/api/wba/v1/users/active").hasAnyAuthority("USER:UPDATE")
                        .requestMatchers(HttpMethod.PATCH, "/api/wba/v1/users/inactive").hasAnyAuthority("USER:UPDATE")
                        .requestMatchers(HttpMethod.PUT, "/api/wba/v1/users/{userId}/reset-password").hasAnyAuthority("USER:UPDATE")

                        // User can access their own data
                        .requestMatchers("/api/wba/v1/profile","/api/wba/v1/setting","/api/wba/v1/**").authenticated()

                        // Admin-only endpoints
                        .requestMatchers("/api/wba/v1/**").hasRole("ADMIN")

                        // Any other request needs authentication
                        .anyRequest().authenticated()
                )
//                .addFilterBefore(apiTransactionFilter, FilterSecurityInterceptor.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler(accessDeniedHandler)
                                .authenticationEntryPoint(unauthorizedHandler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthConverter))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("userAuthProvider")
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setHideUserNotFoundExceptions(false);
        return new ProviderManager(authProvider);
    }
}