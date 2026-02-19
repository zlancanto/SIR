package jpa.dto.exceptions;

import java.time.Instant;

/**
 * Data transfer object ResponseExceptionDto.
 */
public record ResponseExceptionDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
