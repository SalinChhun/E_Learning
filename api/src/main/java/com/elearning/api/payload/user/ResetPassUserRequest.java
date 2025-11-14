package com.elearning.api.payload.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResetPassUserRequest {

    @NotBlank
    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("is_generated_password")
    private Boolean isGeneratedPassword;

    public ResetPassUserRequest(String newPassword, Boolean isGeneratedPassword) {
        this.newPassword = newPassword;
        this.isGeneratedPassword = isGeneratedPassword;
    }
}
