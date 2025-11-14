package com.elearning.api.payload.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@NoArgsConstructor
public class CommonCodeResponse {

    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal value;

    private String name;

    @JsonProperty("parent_code")
    private String parentCode;

    @JsonProperty("created_at")
    private Instant createdAt;

    private String description;

    @Builder
    public CommonCodeResponse(String code, BigDecimal value, String name, String parentCode, Instant createdAt, String description) {
        this.code = code;
        this.value = value;
        this.name = name;
        this.parentCode = parentCode;
        this.createdAt = createdAt;
        this.description = description;
    }
}
