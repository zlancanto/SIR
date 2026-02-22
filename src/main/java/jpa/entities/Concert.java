package jpa.entities;

import jakarta.persistence.*;
import jpa.enums.ConcertStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Concert aggregate created by an organizer and optionally validated by an admin.
 *
 * <p>New concerts start in {@code PENDING_VALIDATION} status and become
 * publicly visible when promoted to {@code PUBLISHED}.</p>
 */
@Entity
@Table(name = "concerts")
public class Concert extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String artist;

    @Column(length = 1024)
    private String description;

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
     * Returns the admin who validated the concert.
     *
     * @return validating admin, or {@code null} while pending
     */
    public Admin getAdmin() {
        return admin;
    }

    /**
     * Sets the admin who validated the concert.
     *
     * @param admin validating admin
     */
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    /**
     * Returns the concert title.
     *
     * @return title shown in listings
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the concert title.
     *
     * @param title title shown in listings
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the artist name.
     *
     * @return artist name, may be {@code null}
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Sets the artist name.
     *
     * @param artist artist name, may be {@code null}
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Returns the concert description.
     *
     * @return concert description, may be {@code null}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the concert description.
     *
     * @param description concert description, may be {@code null}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the scheduled date and time.
     *
     * @return concert schedule
     */
    public Instant getDate() {
        return date;
    }

    /**
     * Sets the scheduled date and time.
     *
     * @param date concert schedule
     */
    public void setDate(Instant date) {
        this.date = date;
    }

    /**
     * Returns the workflow status.
     *
     * @return current concert status
     */
    public ConcertStatus getStatus() {
        return status;
    }

    /**
     * Sets the workflow status.
     *
     * @param status current concert status
     */
    public void setStatus(ConcertStatus status) {
        this.status = status;
    }

    /**
     * Returns the organizer that created this concert.
     *
     * @return linked organizer
     */
    public Organizer getOrganizer() {
        return organizer;
    }

    /**
     * Sets the organizer that created this concert.
     *
     * @param organizer linked organizer
     */
    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    /**
     * Returns the venue where the concert will be held.
     *
     * @return linked venue
     */
    public Place getPlace() {
        return place;
    }

    /**
     * Sets the venue where the concert will be held.
     *
     * @param place linked venue
     */
    public void setPlace(Place place) {
        this.place = place;
    }

    /**
     * Returns tickets currently attached to this concert.
     *
     * @return mutable ticket collection managed by JPA
     */
    public List<Ticket> getTickets() {
        return tickets;
    }

    /**
     * Replaces the ticket collection of this concert.
     *
     * @param tickets replacement ticket collection
     */
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
