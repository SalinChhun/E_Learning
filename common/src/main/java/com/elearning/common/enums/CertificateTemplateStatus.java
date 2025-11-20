package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CertificateTemplateStatus implements GenericEnum<CertificateTemplateStatus, String> {
    DRAFT("1"),
    ACTIVE("2"),
    DELETE("9");

    private final String value;

    CertificateTemplateStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CertificateTemplateStatus fromValue(String value) {
        for(CertificateTemplateStatus status: CertificateTemplateStatus.values()) {
            if(status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return switch (this) {
            case DRAFT -> "Draft";
            case ACTIVE -> "Active";
            case DELETE -> "Delete";
        };
    }

    public static class Converter extends AbstractEnumConverter<CertificateTemplateStatus, String> {
        public Converter() {
            super(CertificateTemplateStatus.class);
        }
    }
}

