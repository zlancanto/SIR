package jpa.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity Customer.
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    @OneToMany(mappedBy = "customer")
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Executes getTickets operation.
     *
     * @return operation result
     */
    public List<Ticket> getTickets() {
        return tickets;
    }

    /**
     * Executes setTickets operation.
     *
     * @param tickets method parameter
     */
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
