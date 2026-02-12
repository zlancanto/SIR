package jpa.controllers;

import jpa.services.interfaces.CustomerService;

public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
}
