package jpa.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity Concert.
 */
@Entity
public class Concert extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String artist;
    private Instant date;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Executes getAdmin operation.
     *
     * @return operation result
     */
    public Admin getAdmin() {
        return admin;
    }

    /**
     * Executes setAdmin operation.
     *
     * @param admin method parameter
     */
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    /**
     * Executes getTitle operation.
     *
     * @return operation result
     */
    public String getTitle() {
        return title;
    }

    /**
     * Executes setTitle operation.
     *
     * @param title method parameter
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Executes getArtist operation.
     *
     * @return operation result
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Executes setArtist operation.
     *
     * @param artist method parameter
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Executes getDate operation.
     *
     * @return operation result
     */
    public Instant getDate() {
        return date;
    }

    /**
     * Executes setDate operation.
     *
     * @param date method parameter
     */
    public void setDate(Instant date) {
        this.date = date;
    }

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
