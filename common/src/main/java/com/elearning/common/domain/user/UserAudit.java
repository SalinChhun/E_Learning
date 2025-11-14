package com.elearning.common.domain.user;

import com.elearning.common.domain.CreatableEntity;
import com.elearning.common.enums.AuditAction;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_usr_audit")
@NoArgsConstructor
public class UserAudit extends CreatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usr_id",nullable = false)
    private Long userId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "old_value", length = 50)
    private String oldValue;

    @Column(name = "new_value", length = 50)
    private String newValue;

    @Builder
    public UserAudit(Long id, Long userId, AuditAction action, String oldValue, String newValue) {
        this.id = id;
        this.userId = userId;
        this.action = action.getValue();
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
