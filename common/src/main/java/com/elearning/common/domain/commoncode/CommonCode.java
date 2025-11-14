package com.elearning.common.domain.commoncode;

import com.elearning.common.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "tb_comm_cd")
@IdClass(CommonCodePK.class)
@EntityListeners(AuditingEntityListener.class)
public class CommonCode  {

    @Id
    @Column(name = "grp_cd", length = 50)
    private String groupCode;
    @Id
    @Column(name = "cd", length = 10)
    private String code;

    @Column(name = "name",length = 50)
    private String name;

    @Column(name = "p_cd", precision = 10)
    private String parentCode;

    @Column(name = "is_default")
    @JdbcTypeCode(Types.BOOLEAN)
    @ColumnDefault("false")
    private Boolean isDefault;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Instant createdAt;

    @Column
    private String description;

    @Column(name = "status",nullable = false, length = Types.CHAR)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    @ColumnDefault("'1'")
    private Status status;

    @Builder
    public CommonCode(String groupCode, String code) {
        this.groupCode = groupCode;
        this.code = code;
    }
}
