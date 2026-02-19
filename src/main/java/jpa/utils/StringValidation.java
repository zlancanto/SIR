package jpa.utils;

import jakarta.ws.rs.BadRequestException;

public class StringValidation {
    public static String normalizeRequired(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldName + " is required");
        }
        return value.trim();
    }
}
