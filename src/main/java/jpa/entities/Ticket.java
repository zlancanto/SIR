package jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
@NamedQuery(
        name = "Ticket.findAvailable",
        query = "SELECT t FROM Ticket t WHERE t.sold = false"
)
/**
 * JPA entity Ticket.
 */
public class Ticket extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String barcode;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean sold = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    /**
     * Executes getBarcode operation.
     *
     * @return operation result
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Executes setBarcode operation.
     *
     * @param barcode method parameter
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * Executes getPrice operation.
     *
     * @return operation result
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Executes setPrice operation.
     *
     * @param price method parameter
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Executes getCustomer operation.
     *
     * @return operation result
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Executes setCustomer operation.
     *
     * @param customer method parameter
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Executes getConcert operation.
     *
     * @return operation result
     */
    public Concert getConcert() {
        return concert;
    }

    /**
     * Executes setConcert operation.
     *
     * @param concert method parameter
     */
    public void setConcert(Concert concert) {
        this.concert = concert;
    }

    /**
     * Executes getPlace operation.
     *
     * @return operation result
     */
    public Place getPlace() {
        return place;
    }

    /**
     * Executes setPlace operation.
     *
     * @param place method parameter
     */
    public void setPlace(Place place) {
        this.place = place;
    }
}
