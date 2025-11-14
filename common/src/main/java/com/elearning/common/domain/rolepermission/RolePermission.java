package com.elearning.common.domain.rolepermission;

import com.elearning.common.domain.Auditable;
import com.elearning.common.domain.permission.Permission;
import com.elearning.common.domain.role.Role;
import com.elearning.common.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Getter
@Setter
@Entity
    @Table(name = "tb_role_perms")
@NoArgsConstructor
public class RolePermission extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perms_id", nullable = false)
    private Permission permission;

    @Column(name = "status", nullable = false, length = 1)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    @ColumnDefault("1")
    private Status status = Status.NORMAL;

    @Builder
    public RolePermission(Role role, Permission permission, Status status) {
        this.role = role;
        this.permission = permission;
        this.status = status;
    }
}
