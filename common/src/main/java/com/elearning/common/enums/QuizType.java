package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuizType implements GenericEnum<QuizType, String> {
    QUIZ("1"),
    EXAM("2");

    private final String value;

    QuizType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static QuizType fromValue(String value) {
        for(QuizType type: QuizType.values()) {
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
            case QUIZ -> "Quiz";
            case EXAM -> "Exam";
        };
    }

    public static class Converter extends AbstractEnumConverter<QuizType, String> {
        public Converter() {
            super(QuizType.class);
        }
    }
}

