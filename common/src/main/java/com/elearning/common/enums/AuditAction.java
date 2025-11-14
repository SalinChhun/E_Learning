package com.elearning.common.enums;

import com.elearning.common.components.AbstractEnumConverter;
import com.elearning.common.components.GenericEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;


/**
 * A class can be used for getting BillType enum
 */
public enum AuditAction implements GenericEnum<AuditAction, String> {
    CREATE("Create"),
    UPDATE("Update"),
    DELETE("Delete"),
    LOGIN("Login"),
    PASSWORD_RESET("Password reset"),
    ROLE_UPDATE("Role Updated"),
    EMAIL_UPDATE("Email Updated"),
    PASSWORD_CHANGE("Password changed"),
    STATUS_CHANGE("Status changed"),
    USERNAME_UPDATE("Username changed"),
    ;

    private final String value;

    AuditAction(String value) {
        this.value = value;
    }

    /**
     * Method getValue  : Get Enum value
     * @return Enum value
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Method getLabel  : Get Enum Label
     * @return Enum Label
     */
    @Override
    public String getLabel() {

        return "Unknown";
    }

    /**
     * Method fromValue : Check Enum value
     *
     * @param value  value that have to check
     * @return enum value
     */
    @JsonCreator
    public static AuditAction fromValue(String value) {
        return Stream.of(AuditAction.values()).filter(targetEnum -> targetEnum.value.equals(value)).findFirst().orElse(null);
    }

    public static class Converter extends AbstractEnumConverter<AuditAction, String> {

        public Converter() {
            super(AuditAction.class);
        }

    }
}
