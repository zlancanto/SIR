package jpa.dto.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Data transfer object ResponseExceptionDto.
 */
@Schema(name = "ErrorResponse")
public record ResponseExceptionDto(
        @Schema(description = "Error timestamp", type = "string", format = "date-time")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "404")
        int status,
        @Schema(description = "HTTP status reason", example = "Not Found")
        String error,
        @Schema(description = "Error message", example = "Concert not found")
        String message,
        @Schema(description = "Request path", example = "/concerts/123")
        String path
) {
}
