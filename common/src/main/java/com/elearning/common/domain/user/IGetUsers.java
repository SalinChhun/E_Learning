package com.elearning.common.domain.user;

import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

public interface IGetUsers {

    @Value("#{target.id}")
    Long getId();

    @Value("#{target.full_name}")
    String getFullName();

    @Value("#{target.username}")
    String getUsername();

    @Value("#{target.email}")
    String getEmail();

    @Value("#{target.status}")
    String getStatus();

    @Value("#{target.image}")
    String getImage();

    @Value("#{target.role}")
    String getRole();

    @Value("#{target.department}")
    String getDepartment();

    @Value("#{target.last_log}")
    String getLastLog();

    @Value("#{target.created_at}")
    Instant getCreatedAt();

    @Value("#{target.last_login}")
    Instant getLastLogin();

}
