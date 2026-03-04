package jpa.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "AdminSummary")
public record ResponseAdminSummaryDto(
        @Schema(description = "Admin identifier", type = "string", format = "uuid")
        UUID id,
        @Schema(description = "Admin email address", format = "email", example = "admin@example.com")
        String email,
        @Schema(description = "Admin first name", example = "Alice")
        String firstName,
        @Schema(description = "Admin last name", example = "Martin")
        String lastName,
        @Schema(
                description = "Persisted role value",
                allowableValues = {"ROLE_ADMIN"},
                example = "ROLE_ADMIN"
        )
        String role,
        @Schema(description = "Account creation timestamp", type = "string", format = "date-time")
        Instant createdAt
) {
}
