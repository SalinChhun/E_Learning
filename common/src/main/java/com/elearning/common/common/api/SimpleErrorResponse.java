package com.elearning.common.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Format 1: Simple code and status
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleErrorResponse {
    private String code;
    private String status;
}

