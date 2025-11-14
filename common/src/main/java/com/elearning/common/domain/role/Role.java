package com.elearning.common.domain.role;

import com.elearning.common.domain.Auditable;
import com.elearning.common.enums.RoleStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;


@Getter
@Setter
@Entity
@Table(name = "tb_role")
@NoArgsConstructor
public class Role extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 25, nullable = false, name = "name")
    private String name;

    private String description;

    @Column(name = "status", nullable = false, length = 1)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = RoleStatus.Converter.class)
    private RoleStatus status = RoleStatus.NORMAL;

    @Builder
    public Role(Long id, String name, String description, RoleStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }
}
