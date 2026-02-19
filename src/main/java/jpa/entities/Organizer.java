package jpa.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity Organizer.
 */
@Entity
@DiscriminatorValue("ORGANIZER")
public class Organizer extends User {
    @OneToMany(mappedBy = "organizer")
    private List<Concert> concerts = new ArrayList<>();

    /**
     * Executes getConcerts operation.
     *
     * @return operation result
     */
    public List<Concert> getConcerts() {
        return concerts;
    }

    /**
     * Executes setConcerts operation.
     *
     * @param concerts method parameter
     */
    public void setConcerts(List<Concert> concerts) {
        this.concerts = concerts;
    }
}
