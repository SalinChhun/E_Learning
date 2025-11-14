package com.elearning.common.enums;

import com.elearning.common.exception.BusinessException;
import lombok.Getter;

import java.text.MessageFormat;

@Getter
public enum StatusCode {
    // Success codes
    SUCCESS(200, "SUCCESSFUL", 200),
    CREATED(201, "Created successfully", 201),
    ACCEPTED(202, "Request accepted", 202),

    //CUSTOM 404
    USER_NOT_FOUND(404, "User Not Found", 404),
    PROVIDER_NOT_FOUND(404, "Provider Not Found", 404),
    BAD_CREDENTIALS(452, "Password is incorrect", 452),
    USER_DISABLED(453, "User account was deactivated", 453),

    //  USER
    USERNAME_ALREADY_EXIST(400,"Username already exists", 400),

    // TOKEN
    TOKEN_EXPIRED(1004, "Token has expired", 401),
    INVALID_TOKEN(1005, "Invalid token or token has expired", 401),
    TOKEN_NOT_FOUND(404, "Token Not Found", 404),

    // ROLE
    ROLE_NOT_FOUND(404, "Role Not Found", 404),

    // FILE
    IMAGE_CANNOT_BE_EMPTY(400, "Image cannot be empty", 400),

    // COMMON CODE
    COMMON_CODE_NOT_FOUND(404, "Common code Not Found", 404),

    // COURSE
    COURSE_NOT_FOUND(404, "Course Not Found", 404),
    COURSE_CATEGORY_NOT_FOUND(404, "Course Category Not Found", 404),
    ENROLLMENT_NOT_FOUND(404, "Course Enrollment Not Found", 404),
    LESSON_NOT_FOUND(404, "Lesson Not Found", 404),
    ENROLLMENT_ALREADY_EXISTS(409, "User is already enrolled in this course", 409),
    CATEGORY_ALREADY_EXISTS(409, "Category with this name already exists", 409),

    // QUIZ
    QUIZ_NOT_FOUND(404, "Quiz Not Found", 404),
    QUESTION_NOT_FOUND(404, "Question Not Found", 404),

    // Client error codes
    BAD_REQUEST(400, "Bad request", 400),
    UNAUTHORIZED(403, "Unauthorized", 401),
    FORBIDDEN(403, "Access denied", 403),
    NOT_FOUND(404, "Resource not found", 404),
    METHOD_NOT_ALLOWED(405, "Method not allowed", 405),
    CONFLICT(409, "Conflict", 409),
    UNPROCESSABLE_ENTITY(422, "Unprocessable entity", 422),
    TOO_MANY_REQUESTS(429, "Too many requests", 429),

    // Server error codes
    INTERNAL_SERVER_ERROR(500, "Unknown error.", 500),
    NOT_IMPLEMENTED(501, "Not implemented", 501),
    BAD_GATEWAY(502, "Bad gateway", 502),
    SERVICE_UNAVAILABLE(503, "Service unavailable", 503),
    GATEWAY_TIMEOUT(504, "Gateway timeout", 504),
    ;

    private final int code;
    private final String message;
    private final int httpCode;

    StatusCode(int code, String message, int httpCode) {
        this.code = code;
        this.message = message;
        this.httpCode = httpCode;
    }

    public static StatusCode fromCode(int code) {
        for (StatusCode statusCode : StatusCode.values()) {
            if (statusCode.getCode() == code) {
                return statusCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
    public String formatMessage(Object... args) {
        return MessageFormat.format(message, args);
    }
    public BusinessException exception(Object... args) {
        return new BusinessException(this, args);
    }
}
