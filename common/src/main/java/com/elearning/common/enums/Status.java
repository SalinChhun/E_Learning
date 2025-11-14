package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A class can be used for getting UserStatus enum
 */
public enum Status implements GenericEnum<Status, String> {
    NORMAL("1"),
    DISABLE("2"),
    DELETE("9")
    ;

    private final String value;

    Status(String value) {
        this.value = value;
    }

    /**
     * Method fromValue : Check Enum value
     *
     * @param value value that have to check
     * @return enum value
     */
    @JsonCreator
    public static Status fromValue(String value) {
        for(Status my: Status.values()) {
            if(my.value.equals(value)) {
                return my;
            }
        }

        return null;
    }

    /**
     * Method getValue : Get Enum value
     * @return Enum value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Method getLabel : Get Enum Label
     * @return Enum Label
     */
    @Override
    public String getLabel() {
        return switch (this) {
            case NORMAL -> "normal";
            case DISABLE -> "disable";
            case DELETE -> "delete";
            default -> "default";
        };
    }

    public static class Converter extends AbstractEnumConverter<Status, String> {

        public Converter() {
            super(Status.class);
        }

    }

}
