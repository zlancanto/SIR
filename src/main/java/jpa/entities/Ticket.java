package jpa.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tickets")
@NamedQuery(
        name = "Ticket.findAvailable",
        query = "SELECT t FROM Ticket t WHERE t.sold = false"
)
public class Ticket extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String barcode;

    @Column(nullable = false)
    private Double price;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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