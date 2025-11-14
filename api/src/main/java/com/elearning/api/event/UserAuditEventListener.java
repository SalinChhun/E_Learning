package com.elearning.api.event;

import com.elearning.common.domain.user.UserAudit;
import com.elearning.common.domain.user.UserAuditRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserAuditEventListener {
    private final UserAuditRepository userAuditRepository;

    public UserAuditEventListener(UserAuditRepository userAuditRepository) {
        this.userAuditRepository = userAuditRepository;
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserChangeEvent(UserChangeEvent event) {
        UserAudit audit = UserAudit.builder()
                .userId(event.getUserId())
                .action(event.getAction())
                .oldValue(event.getOldValue())
                .newValue(event.getNewValue())
                .build();

        userAuditRepository.save(audit);
    }
}