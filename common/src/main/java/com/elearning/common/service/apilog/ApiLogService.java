package com.elearning.common.service.apilog;

import com.elearning.common.domain.transaction.ApiLog;

import java.time.Instant;

public interface ApiLogService {
    // com.elearning.common.payload.CBS -> FEP logging
    ApiLog saveCBSToFEPLog(String requestId, String serviceName, String requestUrl,
                           String httpMethod, String requestHeaders, String requestData,
                           String userIpAddress, String userAgent, Instant requestAt);

    void updateCBSToFEPLogResponse(Long logId, Integer httpStatus, String responseHeaders,
                                   String responseData, Instant responseAt, Long durationMs,
                                   String errorCode, String errorMessage, String errorCategory);

    // FEP -> Client logging
    ApiLog saveFEPToClientLog(String requestId, String serviceName, String targetSystem,
                              String requestUrl, String httpMethod, String requestHeaders,
                              String requestData, String userIpAddress, String userAgent,
                              Instant requestAt, Long parentLogId);

    void updateFEPToClientLogResponse(Long logId, Integer httpStatus, String responseHeaders,
                                      String responseData, Instant responseAt, Long durationMs,
                                      String errorCode, String errorMessage, String errorCategory);

}