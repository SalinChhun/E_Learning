package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ClientEndpointStatus implements GenericEnum<ClientEndpointStatus, String> {
    ENABLE("1"),
    DISABLE("2"),
    DELETED("9")
            ;

    private final String value;

    ClientEndpointStatus(String value) {
        this.value = value;
    }

    /**
     * Method fromValue : Check Enum value
     *
     * @param value value that have to check
     * @return enum value
     */
    @JsonCreator
    public static ClientEndpointStatus fromValue(String value) {
        for(ClientEndpointStatus my: ClientEndpointStatus.values()) {
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

    public static class Converter extends AbstractEnumConverter<ClientEndpointStatus, String> {
        public Converter() {
            super(ClientEndpointStatus.class);
        }
    }
}
