package com.elearning.common.domain;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

@MappedSuperclass
public abstract class AbstractEntity<ID> implements Persistable<ID> {

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    @PostPersist
    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
