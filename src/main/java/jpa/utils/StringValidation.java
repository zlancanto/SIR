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

    /**
     * Returns the first non-blank value among ordered candidates.
     *
     * @param values ordered candidates
     * @return first non-blank value, or {@code null} if none is set
     */
    public static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
