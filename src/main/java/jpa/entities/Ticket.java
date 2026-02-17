package jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
@NamedQuery(
        name = "Ticket.findAvailable",
        query = "SELECT t FROM Ticket t WHERE t.sold = false"
)
public class Ticket extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String barcode;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean sold = false;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Concert getConcert() {
        return concert;
    }

    public void setConcert(Concert concert) {
        this.concert = concert;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}