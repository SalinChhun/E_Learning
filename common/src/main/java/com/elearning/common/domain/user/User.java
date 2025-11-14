package com.elearning.common.domain.user;

import com.elearning.common.domain.Auditable;
import com.elearning.common.domain.dept.Department;
import com.elearning.common.domain.role.Role;
import com.elearning.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tb_usr")
@NoArgsConstructor
@DynamicUpdate
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", length = 30, nullable = false)
    private String email;

    @Column(name = "username",length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "status", nullable = false, length = 1)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = UserStatus.Converter.class)
    @ColumnDefault("1")
    private UserStatus status = UserStatus.ACTIVE;

    private String image;

    @Column(name = "is_sys_gen")
    private boolean isSystemGenerate;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "last_pasw_change")
    private Instant lastPasswordChange;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Version
    private Long version;

    @Builder
    public User(Long id, String fullName, String email, String username, String password, UserStatus status, String image, boolean isSystemGenerate, Department department, Role role, Instant lastPasswordChange, Instant lastLogin, Long version) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = status;
        this.image = image;
        this.isSystemGenerate = isSystemGenerate;
        this.department = department;
        this.role = role;
        this.lastPasswordChange = lastPasswordChange;
        this.lastLogin = lastLogin;
        this.version = version;
    }

    public boolean isActive(){
        return this.status.equals(UserStatus.ACTIVE);
    }
}