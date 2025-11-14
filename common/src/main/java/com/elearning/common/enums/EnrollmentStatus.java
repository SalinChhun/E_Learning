package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnrollmentStatus implements GenericEnum<EnrollmentStatus, String> {
    PENDING("1"),
    ENROLLED("2"),
    IN_PROGRESS("3"),
    COMPLETED("4"),
    REJECTED("9");

    private final String value;

    EnrollmentStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EnrollmentStatus fromValue(String value) {
        for(EnrollmentStatus status: EnrollmentStatus.values()) {
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
            case PENDING -> "Pending Request";
            case ENROLLED -> "Enrolled";
            case IN_PROGRESS -> "In Progress";
            case COMPLETED -> "Completed";
            case REJECTED -> "Rejected";
        };
    }

    public static class Converter extends AbstractEnumConverter<EnrollmentStatus, String> {
        public Converter() {
            super(EnrollmentStatus.class);
        }
    }
}

