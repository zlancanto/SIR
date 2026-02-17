package jpa.dto.customer;

import java.util.UUID;

public record ResponseCustomerDto(
        UUID id,
        String email,
        String firstName,
        String lastName
) {
}
