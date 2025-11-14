package com.elearning.api.payload.auth;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(

        @NotNull
        String username,

        @NotNull
        String password
) {
}
