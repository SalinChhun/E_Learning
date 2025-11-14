package com.elearning.api.payload.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String image;

    private String status;

    private String role;

    private String department;

    private String lastLog;

    private Instant createdAt;

    private Instant lastLogin;

    @Builder
    public UserResponse(Long id, String username, String email, String fullName, String image, String role, String department, String lastLog, String status, Instant createdAt, Instant lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.image = image;
        this.role = role;
        this.department = department;
        this.lastLog = lastLog;
        this.status = status;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }
}

