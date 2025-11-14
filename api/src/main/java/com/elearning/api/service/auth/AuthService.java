package com.elearning.api.service.auth;

import com.elearning.api.payload.auth.LoginRequest;
import com.elearning.api.payload.auth.LogoutTokenRequest;
import com.elearning.api.payload.auth.TokenRefreshRequest;

public interface AuthService {
    Object login(LoginRequest payload) throws Exception;

    Object refreshToken(TokenRefreshRequest payload);

    void logout(LogoutTokenRequest payload);
}