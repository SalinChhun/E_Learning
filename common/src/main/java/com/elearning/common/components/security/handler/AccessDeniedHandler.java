package com.elearning.common.components.security.handler;

import com.elearning.common.common.api.ApiResponse;
import com.elearning.common.common.api.ApiStatus;
import com.elearning.common.common.api.EmptyJsonResponse;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.util.ObjectUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        try (ServletServerHttpResponse res = new ServletServerHttpResponse(response)) {
            res.setStatusCode(HttpStatus.FORBIDDEN);
            res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            ApiStatus apiStatus = new ApiStatus(StatusCode.FORBIDDEN);
            var apiResponse = new ApiResponse<>(apiStatus, new EmptyJsonResponse());
            res.getBody().write(Objects.requireNonNull(ObjectUtils.writeValueAsString(apiResponse)).getBytes());
        }
    }
}
