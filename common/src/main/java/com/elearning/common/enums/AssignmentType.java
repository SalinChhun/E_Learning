package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AssignmentType implements GenericEnum<AssignmentType, String> {
    INDIVIDUAL("01"),
    TEAM("02");

    private final String value;

    AssignmentType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AssignmentType fromValue(String value) {
        for(AssignmentType type: AssignmentType.values()) {
            if(type.value.equals(value)) {
                return type;
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
            case INDIVIDUAL -> "Individual";
            case TEAM -> "Team";
        };
    }

    public static class Converter extends AbstractEnumConverter<AssignmentType, String> {
        public Converter() {
            super(AssignmentType.class);
        }
    }
}




