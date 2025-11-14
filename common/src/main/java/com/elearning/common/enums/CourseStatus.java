package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CourseStatus implements GenericEnum<CourseStatus, String> {
    DRAFT("1"),
    PUBLISHED("2"),
    ARCHIVED("9");

    private final String value;

    CourseStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CourseStatus fromValue(String value) {
        for(CourseStatus status: CourseStatus.values()) {
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
            case PUBLISHED -> "Published";
            case ARCHIVED -> "Archived";
        };
    }

    public static class Converter extends AbstractEnumConverter<CourseStatus, String> {
        public Converter() {
            super(CourseStatus.class);
        }
    }
}

