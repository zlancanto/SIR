package jpa.dto.customer;

public record CreateCustomerRequestDto(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
