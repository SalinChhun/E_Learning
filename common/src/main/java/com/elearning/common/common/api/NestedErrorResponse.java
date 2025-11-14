package com.elearning.common.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Format 3: Nested error object
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NestedErrorResponse {
    private ErrorInfo error;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorInfo {
        private String code;
        private String message;
    }
}
