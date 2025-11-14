package com.elearning.common.components.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.MessageFormat;

@Getter
@AllArgsConstructor
public enum StatusCode {

    // Success (0-999)
    SUCCESS(200, "Success"),

    // Common errors (1000-1999)
    INTERNAL_SERVER_ERROR(1000, "Internal server error"),
    INVALID_REQUEST(1001, "Invalid request"),
    RESOURCE_NOT_FOUND(1002, "Resource not found: {0}"),
    VALIDATION_ERROR(1003, "Validation error: {0}"),

    USER_DISABLED(1004, "User account is disabled"),
    USER_LOCKED(1005, "User account is locked"),
    USER_ACTIVATION_REQUIRED(1006, "User account activation is required"),

    // Authentication errors (2000-2999)
    UNAUTHORIZED(2000, "Unauthorized"),
    INVALID_CREDENTIALS(2001, "Invalid username or password"),
    TOKEN_EXPIRED(2002, "Token has expired"),
    ACCOUNT_LOCKED(2003, "Account is locked"),
    FORBIDDEN(2004, "You are not authorized to perform this action"),
    CLIENT_NOT_FOUND(2005, "Client not found"),


    // User related errors (3000-3999)
    USER_NOT_FOUND(3000, "User not found with id: {0}"),

    // Business validation errors (4000-4999)
    SERVICE_ID_NOT_FOUND(4000, "Service ID not found: {0}"),
    SERVICE_ID_INVALID(4001, "Invalid service ID: ''{0}''"),
    SERVICE_ID_EXPIRED(4002, "Service ID ''{0}'' is no longer valid due to expiration."),

    // API Client Errors (5000-5999)
    API_CLIENT_ERROR(5000, "API client error. Status: {0}, Message: {1}"),
    API_SERVER_ERROR(5001, "API server error. Status: {0}, Message: {1}"),
    API_TIMEOUT_ERROR(5002, "API request timeout"),
    API_CONNECTION_ERROR(5003, "API connection error: {0}"),
    API_AUTHENTICATION_ERROR(5004, "API authentication failed")
    ;


    private final int code;
    private final String message;

    public String formatMessage(Object... args) {
        return MessageFormat.format(message, args);
    }

//    public BusinessException exception(Object... args) {
//        return new BusinessException(this, args);
//    }


}
