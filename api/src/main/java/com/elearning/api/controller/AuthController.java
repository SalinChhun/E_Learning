package com.elearning.api.controller;

import com.elearning.api.payload.auth.LoginRequest;
import com.elearning.api.payload.auth.LogoutTokenRequest;
import com.elearning.api.payload.auth.TokenRefreshRequest;
import com.elearning.api.service.auth.AuthService;
import com.elearning.common.common.RestApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wba/v1/auth")
@RequiredArgsConstructor
public class AuthController extends RestApiResponse {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) throws Exception {
        var token = authService.login(request);
        return ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutTokenRequest payload) {
        authService.logout(payload);
        return ok();
    }
}