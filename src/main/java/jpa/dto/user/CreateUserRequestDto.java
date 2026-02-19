package jpa.dto.user;

/**
 * Data transfer object CreateUserRequestDto.
 */
public record CreateUserRequestDto(
        String email,
        String password,
        String firstName,
        String lastName,
        String role
) {
}
