package jpa.dto.user;

public record CreateUserRequestDto(
        String email,
        String password,
        String firstName,
        String lastName,
        String role
) {
}
