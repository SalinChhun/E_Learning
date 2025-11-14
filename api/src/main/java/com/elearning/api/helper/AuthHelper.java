package com.elearning.api.helper;

import com.elearning.common.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Helper class for authentication and user/partner-related operations
 */
@Component
public class AuthHelper {

    /**
     * Gets the current authenticated user's ID from the security context or JWT token
     *
     * @return the current user ID
     * @throws SecurityException if no authenticated user is found or user ID is missing
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authenticated user found");
        }

        // First check if we have a UserPrincipal
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        }

        // Then check if it's a JWT token
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            Map<String, Object> claims = jwtToken.getToken().getClaims();

            if (claims.containsKey("userId")) {
                try {
                    return Long.valueOf(claims.get("userId").toString());
                } catch (NumberFormatException e) {
                    throw new SecurityException("Invalid user ID format in token", e);
                }
            } else {
                throw new SecurityException("User ID claim not found in token");
            }
        }

        throw new SecurityException("Could not determine user ID from authentication");
    }

    /**
     * Checks if the current entity has the specified role
     * @param roleName the role name to check
     * @return true if the entity has the role, false otherwise
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
        }

        return false;
    }


    /**
     * Checks if the current authenticated entity is a User
     * @return true if the current entity is a user, false otherwise
     */
    public boolean isUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            return principal instanceof UserPrincipal;
        }

        return false;
    }

    /**
     * Gets the type of the current authenticated entity
     * @return "USER", "PARTNER", or "UNKNOWN"
     */
    public String getEntityType() {
        if (isUser()) {
            return "USER";
        } else {
            return "UNKNOWN";
        }
    }
}