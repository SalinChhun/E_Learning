package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus implements GenericEnum<UserStatus, String> {
    ACTIVE("1"),
    INACTIVE("2"),
    DELETED("9")
            ;

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    /**
     * Method fromValue : Check Enum value
     *
     * @param value value that have to check
     * @return enum value
     */
    @JsonCreator
    public static UserStatus fromValue(String value) {
        for(UserStatus my: UserStatus.values()) {
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
            case ACTIVE -> "Active";
            case INACTIVE -> "Inactive";
            default -> "delete";
        };
    }

    public static class Converter extends AbstractEnumConverter<UserStatus, String> {
        public Converter() {
            super(UserStatus.class);
        }
    }
}
