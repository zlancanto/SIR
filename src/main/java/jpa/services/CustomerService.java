package jpa.services;

import jpa.dao.impl.CustomerDao;

public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
}
