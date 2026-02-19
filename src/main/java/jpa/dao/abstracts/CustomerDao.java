package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Customer;

import java.util.Optional;
import java.util.UUID;

/**
 * Abstract DAO contract for CustomerDao.
 */
public abstract class CustomerDao extends AbstractJpaDao<UUID, Customer> {

    protected CustomerDao() {
        super(Customer.class);
    }

    /**
     * Executes findByEmail operation.
     *
     * @param email method parameter
     * @return operation result
     */
    public abstract Optional<Customer> findByEmail(String email);
}
