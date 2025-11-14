package com.elearning.common.components.security;

import com.elearning.common.components.logging.AppLogManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Central configuration for authentication managers
 * This separates the authentication managers from security filter chain configuration
 * to avoid circular dependencies
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager for username/password authentication (User entity)
     */
    @Bean(name = "userAuthProvider")
    @Primary
    public AuthenticationManager userAuthenticationManager() {
        AppLogManager.info("Creating userAuthProvider bean with: " );
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return new ProviderManager(authProvider);
    }

    /**
     * Authentication manager for API key authentication (Client entity)
     */
//    @Bean(name = "clientAuthProvider")
//    public AuthenticationManager clientAuthenticationManager() {
//        AppLogManager.info("Creating clientAuthProvider bean with: " + clientAuthenticationProvider.getClass().getName());
//        return new ProviderManager(Collections.singletonList(clientAuthenticationProvider));
//    }
}