package com.elearning.common.domain.permission;

import com.elearning.common.domain.Auditable;
import com.elearning.common.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Entity
@Table(name = "tb_perms",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"resource", "action"},
                name = "uk_resource_action"))
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Permission extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, name = "name")
    private String name;

    @Column(nullable = false, length = 25)
    private String resource;

    @Column(nullable = false, length = 10)
    private String action;

    private String description;

    @Column(name = "status", nullable = false, length = 1)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    private Status status = Status.NORMAL;

    @Builder
    public Permission(Long id, String name, String resource, String action, String description, Status status) {
        this.id = id;
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
        this.status = status;
    }
}
