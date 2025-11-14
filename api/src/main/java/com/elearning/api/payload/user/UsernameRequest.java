package com.elearning.api.payload.user;

import jakarta.validation.constraints.NotNull;

public record UsernameRequest(
        @NotNull
        String username
) {
}
