package com.elearning.common.enums;


import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

/**
 * A class can be used for getting ServiceProvider enum
 */
public enum ServiceProvider implements GenericEnum<ServiceProvider, String> {
    FEP("FEP"),
    THIRD_PART("THIRD-PART"),
    CBS("CBS")
    ;
    private final String value;

    private ServiceProvider(String value) {
        this.value = value;
    }

    /**
     * Method fromValue : Check Enum value
     *
     * @param value  value that have to check
     * @return enum value
     */
    @JsonCreator
    public static ServiceProvider fromValue(String value) {

        return Stream.of(ServiceProvider.values()).filter(targetEnum -> targetEnum.value.equals(value)).findFirst().orElse(null);

    }

    /**
     * Method getValue  : Get Enum value
     * @return Enum value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Method getLabel  : Get Enum Label
     * @return Enum Label
     */
    public String getLabel() {

        return "Unknown";

    }

    public static class Converter extends AbstractEnumConverter<ServiceProvider, String> {

        public Converter() {
            super(ServiceProvider.class);
        }

    }

}