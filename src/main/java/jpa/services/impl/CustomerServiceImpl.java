package jpa.services.impl;

import jpa.dao.abstracts.CustomerDao;
import jpa.services.interfaces.CustomerService;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerDao customerDao;

    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
}
