package com.elearning.common.service.apilog;

import com.elearning.common.domain.transaction.ApiLog;
import com.elearning.common.domain.transaction.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiLogServiceImpl implements ApiLogService {

    private final ApiLogRepository apiLogRepository;

    @Override
    @Transactional
    public ApiLog saveCBSToFEPLog(String requestId, String serviceName, String requestUrl,
                                  String httpMethod, String requestHeaders, String requestData,
                                  String userIpAddress, String userAgent, Instant requestAt) {
        try {
            ApiLog apiLog = ApiLog.builder()
                    .requestId(requestId)
                    .serviceName(serviceName)
                    .sourceSystem("CBS")
                    .targetSystem("FEP")
                    .httpMethod(httpMethod)
                    .requestUrl(requestUrl)
                    .requestHeaders(requestHeaders)
                    .requestData(requestData)
                    .userIpAddress(userIpAddress)
                    .userAgent(userAgent)
                    .requestAt(requestAt)
                    .build();

            return apiLogRepository.save(apiLog);
        } catch (Exception e) {
            log.error("Failed to save com.elearning.common.payload.CBS->FEP log", e);
            return null;
        }
    }

    @Override
    @Transactional
    public void updateCBSToFEPLogResponse(Long logId, Integer httpStatus, String responseHeaders,
                                          String responseData, Instant responseAt, Long durationMs,
                                          String errorCode, String errorMessage, String errorCategory) {
        try {
            Optional<ApiLog> optionalLog = apiLogRepository.findById(logId);
            if (optionalLog.isPresent()) {
                ApiLog apiLog = optionalLog.get();
                apiLog.setHttpStatus(httpStatus);
                apiLog.setResponseHeaders(responseHeaders);
                apiLog.setResponseData(responseData);
                apiLog.setResponseAt(responseAt);
                apiLog.setDuration(durationMs);
                apiLog.setErrorCode(errorCode);
                apiLog.setErrorMessage(errorMessage);
                apiLog.setErrorCategory(errorCategory);

                apiLogRepository.save(apiLog);
            }
        } catch (Exception e) {
            log.error("Failed to update com.elearning.common.payload.CBS->FEP log response for logId: {}", logId, e);
        }
    }

    @Override
    @Transactional
    public ApiLog saveFEPToClientLog(String requestId, String serviceName, String targetSystem,
                                     String requestUrl, String httpMethod, String requestHeaders,
                                     String requestData, String userIpAddress, String userAgent,
                                     Instant requestAt, Long parentLogId) {
        try {
            ApiLog.ApiLogBuilder builder = ApiLog.builder()
                    .requestId(requestId)
                    .serviceName(serviceName)
                    .sourceSystem("FEP")
                    .targetSystem(targetSystem)
                    .httpMethod(httpMethod)
                    .requestUrl(requestUrl)
                    .requestHeaders(requestHeaders)
                    .requestData(requestData)
                    .userIpAddress(userIpAddress)
                    .userAgent(userAgent)
                    .requestAt(requestAt);

            if (parentLogId != null) {
                Optional<ApiLog> parentLog = apiLogRepository.findById(parentLogId);
                if (parentLog.isPresent()) {
                    builder.parent(parentLog.get());
                }
            }

            ApiLog apiLog = builder.build();
            return apiLogRepository.save(apiLog);
        } catch (Exception e) {
            log.error("Failed to save FEP->Client log", e);
            return null;
        }
    }

    @Override
    @Transactional
    public void updateFEPToClientLogResponse(Long logId, Integer httpStatus, String responseHeaders,
                                             String responseData, Instant responseAt, Long durationMs,
                                             String errorCode, String errorMessage, String errorCategory) {
        try {
            Optional<ApiLog> optionalLog = apiLogRepository.findById(logId);
            if (optionalLog.isPresent()) {
                ApiLog apiLog = optionalLog.get();
                apiLog.setHttpStatus(httpStatus);
                apiLog.setResponseHeaders(responseHeaders);
                apiLog.setResponseData(responseData);
                apiLog.setResponseAt(responseAt);
                apiLog.setDuration(durationMs);
                apiLog.setErrorCode(errorCode);
                apiLog.setErrorMessage(errorMessage);
                apiLog.setErrorCategory(errorCategory);

                apiLogRepository.save(apiLog);
            }
        } catch (Exception e) {
            log.error("Failed to update FEP->Client log response for logId: {}", logId, e);
        }
    }

    private String getFepIpAddress() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}