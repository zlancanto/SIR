package jpa.dto.exceptions;

import java.time.Instant;

public record ResponseExceptionDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
