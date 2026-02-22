package jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Sellable ticket for a single concert.
 *
 * <p>A ticket is available while {@code sold = false} and can later be
 * associated with a customer once purchased.</p>
 */
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    /**
     * Returns the unique ticket barcode.
     *
     * @return barcode value
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Sets the unique ticket barcode.
     *
     * @param barcode barcode value
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * Returns the ticket price.
     *
     * @return price value
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the ticket price.
     *
     * @param price price value
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Returns whether this ticket is already sold.
     *
     * @return sold state
     */
    public Boolean getSold() {
        return sold;
    }

    /**
     * Sets sold state.
     *
     * @param sold sold state
     */
    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    /**
     * Returns the customer owning this ticket.
     *
     * @return linked customer, or {@code null} when the ticket is not sold
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Associates this ticket with a customer.
     *
     * @param customer linked customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Returns the concert this ticket belongs to.
     *
     * @return linked concert
     */
    public Concert getConcert() {
        return concert;
    }

    /**
     * Associates this ticket with a concert.
     *
     * @param concert linked concert
     */
    public void setConcert(Concert concert) {
        this.concert = concert;
    }

}
