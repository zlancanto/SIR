package jpa.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    @OneToMany(mappedBy = "customer")
    private List<Ticket> tickets = new ArrayList<>();

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
