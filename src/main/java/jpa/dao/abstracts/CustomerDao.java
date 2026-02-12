package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Customer;

import java.util.UUID;

public abstract class CustomerDao extends AbstractJpaDao<UUID, Customer> {
}
