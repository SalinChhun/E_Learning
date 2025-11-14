package com.elearning.common.service.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LoggingService {
    String handleLoggingRequest(HttpServletRequest httpServletRequest, Object body);
    String handleLoggingResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body);
}