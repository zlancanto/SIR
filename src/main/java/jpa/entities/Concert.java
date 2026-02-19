package jpa.entities;

import jakarta.persistence.*;
import jpa.enums.ConcertStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity Concert.
 */
@Entity
@Table(name = "concerts")
public class Concert extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String artist;

    @Column(nullable = false)
    private Instant date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConcertStatus status = ConcertStatus.PENDING_VALIDATION;

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
     * Executes getStatus operation.
     *
     * @return operation result
     */
    public ConcertStatus getStatus() {
        return status;
    }

    /**
     * Executes setStatus operation.
     *
     * @param status method parameter
     */
    public void setStatus(ConcertStatus status) {
        this.status = status;
    }

    /**
     * Executes getOrganizer operation.
     *
     * @return operation result
     */
    public Organizer getOrganizer() {
        return organizer;
    }

    /**
     * Executes setOrganizer operation.
     *
     * @param organizer method parameter
     */
    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
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
