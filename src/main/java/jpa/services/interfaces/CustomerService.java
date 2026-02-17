package jpa.services.interfaces;

import jpa.dto.customer.CreateCustomerRequestDto;
import jpa.dto.customer.ResponseCustomerDto;

public interface CustomerService {
    ResponseCustomerDto createAccount(CreateCustomerRequestDto request);
}
