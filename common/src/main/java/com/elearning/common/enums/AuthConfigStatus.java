package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthConfigStatus implements GenericEnum<AuthConfigStatus, String> {
    ENABLE("1"),
    DISABLE("2"),
    DELETED("9")
            ;

    private final String value;

    AuthConfigStatus(String value) {
        this.value = value;
    }

    /**
     * Method fromValue : Check Enum value
     *
     * @param value value that have to check
     * @return enum value
     */
    @JsonCreator
    public static AuthConfigStatus fromValue(String value) {
        for(AuthConfigStatus my: AuthConfigStatus.values()) {
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
            case ENABLE -> "Active";
            case DISABLE -> "Inactive";
            default -> "delete";
        };
    }

    public static class Converter extends AbstractEnumConverter<AuthConfigStatus, String> {
        public Converter() {
            super(AuthConfigStatus.class);
        }
    }
}
