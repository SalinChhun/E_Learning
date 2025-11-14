package com.elearning.common.components.security;

import com.elearning.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (auth == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        String resource = targetDomainObject.toString();
        String action = permission.toString();

        // For API key authentication
//        if (auth.getPrincipal() instanceof ClientPrincipal) {
//            ClientPrincipal principal = (ClientPrincipal) auth.getPrincipal();
//            return principal.hasPermission(resource, action);
//        }

        // For user authentication
        if (auth.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
            return principal.hasPermission(resource, action);
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        return hasPermission(auth, targetType, permission);
    }
}