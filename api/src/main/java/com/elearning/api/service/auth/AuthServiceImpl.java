package com.elearning.api.service.auth;

import com.elearning.api.event.AuditEventPublisher;
import com.elearning.api.payload.auth.LoginRequest;
import com.elearning.api.payload.auth.LoginResponse;
import com.elearning.api.payload.auth.LogoutTokenRequest;
import com.elearning.api.payload.auth.TokenRefreshRequest;
import com.elearning.api.service.user.UserService;
import com.elearning.common.components.security.UserAuthenticationProvider;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import com.elearning.common.security.CustomUserDetailsService;
import com.elearning.common.security.UserPrincipal;
import com.elearning.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserAuthenticationProvider authProvider;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;
    private final AuditEventPublisher auditPublisher;

    @Override
    public Object login(LoginRequest payload) throws Exception {
        Authentication authenticate = authProvider.authenticate(payload.username(), payload.password());
        UserPrincipal securityUser = (UserPrincipal) authenticate.getPrincipal();
        String token = jwtTokenUtil.doGenerateToken(securityUser);
        String refreshToken = jwtTokenUtil.doGenerateRefreshToken(securityUser);

        //  TODO: Update LastLogin User
        userService.updateLastLoginDateTime(securityUser.getUserId());
       return LoginResponse.builder()
               .accessToken(token)
               .refreshToken(refreshToken)
               .expiresIn(jwtTokenUtil.getExpireIn())
               .build();
    }

    @Override
    public Object refreshToken(TokenRefreshRequest payload) {
        // Validate refresh token
        if (!jwtTokenUtil.validateRefreshToken(payload.getRefreshToken())) {
            throw new BusinessException(StatusCode.INVALID_TOKEN);
        }

        // Extract username from token
        String username = jwtTokenUtil.getUsernameFromRefreshToken(payload.getRefreshToken());
        var user = customUserDetailsService.loadUserByUsername(username);

        // Generate new access token
        String newAccessToken = jwtTokenUtil.doGenerateToken(user);

        // Return the response with the new access token and same refresh token
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(payload.getRefreshToken())
                .expiresIn(jwtTokenUtil.getExpireIn())
                .build();
    }

    @Override
    public void logout(LogoutTokenRequest payload) {
        jwtTokenUtil.invalidateToken(payload.getRefreshToken());
        jwtTokenUtil.invalidateToken(payload.getAccessToken());
    }

}