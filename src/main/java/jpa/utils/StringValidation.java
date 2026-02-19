package jpa.utils;

import jakarta.ws.rs.BadRequestException;

/**
 * Utility helper StringValidation.
 */
public class StringValidation {
    /**
     * Executes normalizeRequired operation.
     *
     * @param fieldName method parameter
     * @param value     method parameter
     * @return operation result
     */
    public static String normalizeRequired(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldName + " is required");
        }
        return value.trim();
    }
}
