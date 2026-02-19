package jpa.dto.user;

public record CreateAdminRequestDto(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
