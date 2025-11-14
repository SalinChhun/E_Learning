package com.elearning.common.config;

import com.elearning.common.security.UserPrincipal;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.Optional;

/**
 * Implementation of AuditorAware to provide the current user ID for Spring JPA Auditing.
 * This class is responsible for determining the current auditor (user) for auditing purposes.
 */
@Configuration
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        try {
            // Try to get the current user ID from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            // First check if we have a UserPrincipal
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal) {
                return Optional.of(userPrincipal.getUserId());
            }

            // Then check if it's a JWT token
            if (authentication instanceof JwtAuthenticationToken jwtToken) {
                Jwt jwt = jwtToken.getToken();
                Map<String, Object> claims = jwt.getClaims();

                if (claims.containsKey("userId")) {
                    try {
                        Long userId = Long.valueOf(claims.get("userId").toString());
                        return Optional.of(userId);
                    } catch (NumberFormatException e) {
                        // Invalid user ID format in token
                        return Optional.empty();
                    }
                }
            }
            
            return Optional.empty();
            
        } catch (Exception e) {
            // If we can't determine the current user, return empty
            // This might happen during system operations or when no user is authenticated
            return Optional.empty();
        }
    }
}
