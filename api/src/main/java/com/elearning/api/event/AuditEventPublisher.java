package com.elearning.api.event;

import com.elearning.common.enums.AuditAction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AuditEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public AuditEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishPasswordRestEvent(Long userId) {
        eventPublisher.publishEvent(new UserChangeEvent(userId, AuditAction.PASSWORD_RESET, null, null));
    }

    public void publishLoginEvent(Long userId) {
        eventPublisher.publishEvent(new UserChangeEvent(userId, AuditAction.LOGIN, null, null));
    }

    public void publishAuditActionChangeEvent(Long userId, AuditAction action,String oldStatus, String newStatus) {
        eventPublisher.publishEvent(new UserChangeEvent(userId, action , oldStatus, newStatus));
    }

}