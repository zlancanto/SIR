package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Customer;

import java.util.Optional;
import java.util.UUID;

public abstract class CustomerDao extends AbstractJpaDao<UUID, Customer> {

    protected CustomerDao() {
        super(Customer.class);
    }

    public abstract Optional<Customer> findByEmail(String email);
}
