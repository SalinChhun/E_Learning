package com.elearning.api.event;

import com.elearning.common.enums.AuditAction;
import lombok.Getter;

@Getter
public class UserChangeEvent {
    private final Long userId;
    private final AuditAction action;
    private final String oldValue;
    private final String newValue;

    public UserChangeEvent(Long userId, AuditAction action, String oldValue, String newValue) {
        this.userId = userId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}
