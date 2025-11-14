package com.elearning.common.domain.transaction;

import com.elearning.common.domain.AbstractEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "api_log")
@DynamicInsert
@DynamicUpdate
public class ApiLog extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id")
    private ApiLog parent;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "source_system", length = 50)
    private String sourceSystem;

    @Column(name = "target_system", length = 50)
    private String targetSystem;

    @Column(name = "service_name", length = 100)
    private String serviceName;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "request_url", columnDefinition = "TEXT")
    private String requestUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_headers", columnDefinition = "JSONB")
    private String requestHeaders;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_data", columnDefinition = "JSONB")
    private String requestData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_headers", columnDefinition = "JSONB")
    private String responseHeaders;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_data", columnDefinition = "JSONB")
    private String responseData;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "error_category", length = 50)
    private String errorCategory;

    @Column(name = "user_ip_address", length = 45)
    private String userIpAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "duration_ms")
    private Long duration;

    @Column(name = "retry_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer retry = 0;

    @Column(name = "request_at")
    private Instant requestAt;

    @Column(name = "response_at")
    private Instant responseAt;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(insertable = false, name = "updated_at")
    private Instant updatedAt;

    @Builder(toBuilder = true)
    public ApiLog(ApiLog parent, String requestId, String sourceSystem, String targetSystem, String serviceName, String httpMethod, String requestUrl, String requestHeaders, String requestData, String responseHeaders, String responseData, Integer httpStatus, String errorCode, String errorMessage, String errorCategory, String userIpAddress, String userAgent, Long duration, Instant requestAt, Instant responseAt) {
        this.parent = parent;
        this.requestId = requestId;
        this.sourceSystem = sourceSystem;
        this.targetSystem = targetSystem;
        this.serviceName = serviceName;
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.requestHeaders = requestHeaders;
        this.requestData = requestData;
        this.responseHeaders = responseHeaders;
        this.responseData = responseData;
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorCategory = errorCategory;
        this.userIpAddress = userIpAddress;
        this.userAgent = userAgent;
        this.duration = duration;
        this.requestAt = requestAt;
        this.responseAt = responseAt;
    }
}