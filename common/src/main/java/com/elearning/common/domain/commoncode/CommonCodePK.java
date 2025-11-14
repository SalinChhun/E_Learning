package com.elearning.common.domain.commoncode;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;


@NoArgsConstructor
@Embeddable
@Getter
public class CommonCodePK implements Serializable {

    @Column(name = "grp_cd", length = 50)
    private String groupCode;

    @Column(name = "cd", length = 10)
    private String code;

    public CommonCodePK(String groupCode, String code) {
        this.groupCode = groupCode;
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CommonCodePK that = (CommonCodePK) o;
        return groupCode != null && Objects.equals(groupCode, that.groupCode)
                && code != null && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupCode, code);
    }
}
