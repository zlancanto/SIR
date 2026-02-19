package jpa.dto.user;

/**
 * Data transfer object CreateAdminRequestDto.
 */
public record CreateAdminRequestDto(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
