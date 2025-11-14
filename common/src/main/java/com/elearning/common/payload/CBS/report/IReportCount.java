package com.elearning.common.payload.CBS.report;

import org.springframework.beans.factory.annotation.Value;

public interface IReportCount {

    @Value("#{target.user_count}")
    Long getUserCount();

    @Value("#{target.provider_count}")
    Long getProviderCount();

    @Value("#{target.auth_config_count}")
    Long getAuthConfigCount();

    @Value("#{target.api_management_count}")
    Long getApiManagementCount();
}
