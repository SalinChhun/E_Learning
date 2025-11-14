package com.elearning.common.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Format 2: Code and detail
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailErrorResponse {
    private int code;
    private String detail;
}
