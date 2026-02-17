package jpa.services.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import jpa.dao.abstracts.CustomerDao;
import jpa.dto.customer.CreateCustomerRequestDto;
import jpa.dto.customer.ResponseCustomerDto;
import jpa.entities.Customer;
import jpa.services.interfaces.CustomerService;

import java.util.Locale;
import java.util.regex.Pattern;

public class CustomerServiceImpl implements CustomerService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final CustomerDao customerDao;

    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public ResponseCustomerDto createAccount(CreateCustomerRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        String email = normalizeRequired("email", request.email()).toLowerCase(Locale.ROOT);
        String password = normalizeRequired("password", request.password());
        String firstName = normalizeRequired("firstName", request.firstName());
        String lastName = normalizeRequired("lastName", request.lastName());

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Invalid email format");
        }

        if (password.length() < 8) {
            throw new BadRequestException("Password must contain at least 8 characters");
        }

        if (customerDao.findByEmail(email).isPresent()) {
            throw new ClientErrorException("Email already used", Response.Status.CONFLICT);
        }

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);

        customerDao.save(customer);

        return new ResponseCustomerDto(
                customer.getId(),
                customer.getEmail(),
                customer.getFirstName(),
                customer.getLastName()
        );
    }

    private String normalizeRequired(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldName + " is required");
        }
        return value.trim();
    }
}
