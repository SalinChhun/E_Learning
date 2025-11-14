package com.elearning.common.components.security;

import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import com.elearning.common.security.UserPrincipal;
import com.elearning.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional(readOnly = true)
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract user ID from JWT
        Long userId = jwt.getClaim("userId");
        String username = jwt.getClaimAsString("sub");
        String roleName = jwt.getClaimAsString("role");
        if (roleName == null) {
            roleName = "USER";
        }

        if (!jwtTokenUtil.validateToken(jwt.getTokenValue())) {
            throw new BusinessException(StatusCode.INVALID_TOKEN);
        }

        // Find user in database
        //User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Extract permissions from JWT using "scope" claim
        String scope = jwt.getClaimAsString("scope");
        List<String> permissions = Collections.emptyList();
        if (scope != null) {
            permissions = Arrays.asList(scope.split(" "));
        }

        // Create UserPrincipal
        UserPrincipal userPrincipal = new UserPrincipal(userId,username,roleName, permissions);

        // Create and return authentication token
        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }
}