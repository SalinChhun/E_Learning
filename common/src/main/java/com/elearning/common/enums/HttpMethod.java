package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HttpMethod implements GenericEnum<HttpMethod, String> {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    /**
     * Method fromValue : Check Enum value
     *
     * @param value value that have to check
     * @return enum value
     */
    @JsonCreator
    public static HttpMethod fromValue(String value) {
        for(HttpMethod my: HttpMethod.values()) {
            if(my.value.equals(value)) {
                return my;
            }
        }

        return null;
    }

    /**
     * Method getValue : Get Enum value
     * @return Enum value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Method getLabel : Get Enum Label
     * @return Enum Label
     */
    @Override
    public String getLabel() {
        return switch (this) {
            case GET -> "GET";
            case POST -> "POST";
            case PUT -> "PUT";
            case DELETE -> "DELETE";
            case PATCH -> "PATCH";
            case HEAD -> "HEAD";
            case OPTIONS -> "OPTIONS";
            default -> value;
        };
    }

    public static class Converter extends AbstractEnumConverter<HttpMethod, String> {
        public Converter() {
            super(HttpMethod.class);
        }
    }
}