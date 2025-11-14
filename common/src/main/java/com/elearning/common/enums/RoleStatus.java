package com.elearning.common.enums;


import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A class can be used for getting UserStatus enum
 */
public enum RoleStatus implements GenericEnum<RoleStatus, String> {
    NORMAL("1"),
    DISABLE("9");

    private final String value;

    RoleStatus(String value) {
        this.value = value;
    }

    /**
     * Method fromValue : Check Enum value
     *
     * @param value value that have to check
     * @return enum value
     */
    @JsonCreator
    public static RoleStatus fromValue(String value) {
        for(RoleStatus my: RoleStatus.values()) {
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
            default -> "unknown";
        };
    }

    public static class Converter extends AbstractEnumConverter<RoleStatus, String> {

        public Converter() {
            super(RoleStatus.class);
        }

    }

}
