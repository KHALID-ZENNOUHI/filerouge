package org.dev.filerouge.domain.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE, FEMALE;

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Or throw a custom exception if you want to be strict
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
