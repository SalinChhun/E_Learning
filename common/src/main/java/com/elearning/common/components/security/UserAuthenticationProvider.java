package com.elearning.common.components.security;

import com.elearning.common.components.logging.AppLogManager;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticationProvider {

    @Qualifier("userAuthProvider")
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticate a user with username and password
     */
    public Authentication authenticate(String username, String password) throws BusinessException {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            return auth;
        } catch (Exception e) {
            // Log the original exception for debugging
            AppLogManager.info("Authentication exception: {}" + e.getClass().getName(), e);

            // Determine the appropriate error based on the exception type
            if (e instanceof UsernameNotFoundException ||
                    e.getCause() instanceof UsernameNotFoundException) {
                throw new BusinessException(StatusCode.USER_NOT_FOUND, "User not found");
            } else if (e instanceof BadCredentialsException ||
                    e.getCause() instanceof BadCredentialsException) {
                throw new BusinessException(StatusCode.BAD_CREDENTIALS, "Password is incorrect");
            } else if (e instanceof DisabledException ||
                    e.getCause() instanceof DisabledException) {
                throw new BusinessException(StatusCode.USER_DISABLED, e.getMessage());
            } else {
                throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR, "Authentication error");
            }
        }
    }
}