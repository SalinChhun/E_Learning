package com.elearning.api.payload.user;

import com.elearning.common.enums.UserStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRequest {

    @NotBlank
    private String fullName;

    private String email;

    @NotNull
    @Positive
    private Long roleId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;


    private String department;


    private UserStatus status;


    private String image;


    private boolean systemGenerate;


}
