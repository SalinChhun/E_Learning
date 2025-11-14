package com.elearning.common.service;

import com.elearning.common.domain.transaction.ApiLog;
import com.elearning.common.domain.transaction.ApiLogRepository;
import com.elearning.common.enums.ServiceProvider;
import com.elearning.common.exception.BusinessException;
import com.elearning.common.util.HttpUtils;
import com.elearning.common.util.ObjectUtils;
import com.elearning.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestLoggingService {

    private final HttpServletRequest request;
    private final ApiLogRepository apiLogRepository;
    private final ObjectUtils objectUtils;

    public ApiLog begin(String rawBody) {

        ApiLog apiLog = ApiLog.builder()
                .requestId(MDC.get("cbsGuid"))
                .serviceName(MDC.get("svcID"))
                .sourceSystem(ServiceProvider.CBS.getValue())
                .targetSystem(ServiceProvider.FEP.getValue())
                .httpMethod(request.getMethod())
                .requestUrl(request.getRequestURL().toString())
                .requestHeaders(objectUtils.writeValueAsString(HttpUtils.getHeadersInfo(request)))
                .requestData(objectUtils.maskSensitiveData(rawBody))
                .userIpAddress(this.getClientIP())
                .userAgent(HttpUtils.getUserAgent(request))
                .requestAt(Instant.now())
                .build();

        ApiLog entity = apiLogRepository.save(apiLog);

        request.setAttribute("PARENT_TRX", entity);

        return entity;
    }

    public ApiLog begin(HttpRequest httpRequest, ApiLog parent, String rawBody) {

          ApiLog apiLog = ApiLog.builder()
                .parent(parent)
                .requestId(MDC.get("cbsGuid"))
                .serviceName(MDC.get("svcID"))
                .sourceSystem(ServiceProvider.FEP.getValue())
                .targetSystem(MDC.get("userId"))
                .httpMethod(httpRequest.getMethod().toString())
                .requestUrl(httpRequest.getURI().toString())
                .requestHeaders(objectUtils.writeValueAsString(HttpUtils.getHeadersInfo(request)))
                .requestData(objectUtils.maskSensitiveData(rawBody))
                .userIpAddress(this.getClientIP())
                .userAgent(HttpUtils.getUserAgent(request))
                .requestAt(Instant.now())
                .build();

        return apiLogRepository.save(apiLog);
    }

    public void end(ApiLog apiLog, Map<String, Object> response) {
        updateSuccess(apiLog, objectUtils.writeValueAsString(response));
    }

    public void end(ApiLog apiLog, String response){
        updateSuccess(apiLog, response);
    }

    public void end(ClientHttpResponse response, ApiLog apiLog) throws IOException {

        if(response.getStatusCode().is2xxSuccessful()){
            updateSuccess(apiLog, HttpUtils.getResponseBody(response));
        }
        else {

            HttpStatusCode statusCode = response.getStatusCode();
            HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());

            String errorCategory;

            if (statusCode.is4xxClientError()) {
                errorCategory = "CLIENT_ERROR";
            } else if (statusCode.is5xxServerError()) {
                errorCategory = "SERVER_ERROR";
            } else {
                errorCategory = "UNKNOWN_ERROR";
            }

            apiLog.setHttpStatus(statusCode.value());
            apiLog.setErrorCategory(errorCategory);

            apiLog.setResponseAt(Instant.now());
            apiLog.setDuration(Duration.between(apiLog.getRequestAt(), apiLog.getResponseAt()).toMillis());
            apiLog.setResponseData(objectUtils.maskSensitiveData(HttpUtils.getResponseBody(response)));
            apiLog.setResponseHeaders(objectUtils.writeValueAsString(response.getHeaders()));

            apiLog.setErrorCode("" + statusCode.value());
            apiLog.setErrorMessage(Objects.isNull(httpStatus) ? "Unknown error" : httpStatus.getReasonPhrase());

            apiLogRepository.updateError(apiLog);
        }
    }

    private void updateSuccess(ApiLog apiLog, String response) {

        apiLog.setHttpStatus(200);
        apiLog.setResponseAt(Instant.now());
        apiLog.setResponseData(objectUtils.maskSensitiveData(response));
        apiLog.setDuration(Duration.between(apiLog.getRequestAt(), apiLog.getResponseAt()).toMillis());

        apiLogRepository.updateSuccess(apiLog);
    }

    public void endSpansAndThrow(ApiLog apiLog, IOException e) {

        apiLog.setResponseAt(Instant.now());
        apiLog.setDuration(Duration.between(apiLog.getRequestAt(), apiLog.getResponseAt()).toMillis());

        apiLog.setHttpStatus(500);
        apiLog.setErrorCategory("NETWORK_ERROR");

        apiLog.setErrorCode(e.getClass().getSimpleName());
        apiLog.setErrorMessage(StringUtils.defaultIfBlank(e.getLocalizedMessage(), "An unknown error occurred."));

        apiLogRepository.updateError(apiLog);
    }

    public void endAndThrow(ApiLog apiLog, Map<String, Object> response, Throwable e) {

        apiLog.setResponseAt(Instant.now());
        apiLog.setResponseData(objectUtils.maskSensitiveData(objectUtils.writeValueAsString(response)));
        apiLog.setDuration(Duration.between(apiLog.getRequestAt(), apiLog.getResponseAt()).toMillis());

        apiLog.setHttpStatus(500);
        apiLog.setErrorCategory("INTERNAL_SERVER_ERROR");

        String errorCode = "UNKNOWN_ERROR";
        String errorMessage = StringUtils.defaultIfBlank(e.getLocalizedMessage(), "An unknown error occurred.");

        if(e instanceof BusinessException businessException) {
            errorCode = String.valueOf(businessException.getErrorCode());
            errorMessage = businessException.getMessage();
        } else {
            // Log the exception details
        }

        apiLog.setErrorCode(errorCode);
        apiLog.setErrorMessage(errorMessage);

        apiLogRepository.updateError(apiLog);
    }


    private String getClientIP() {
        if (request == null) return "UNKNOWN";

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

}
