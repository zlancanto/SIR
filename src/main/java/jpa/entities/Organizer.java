package jpa.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizers")
@DiscriminatorValue("ORGANIZER")
public class Organizer extends User {
    @OneToMany(mappedBy = "organizer")
    private List<Concert> concerts = new ArrayList<>();

    public List<Concert> getConcerts() {
        return concerts;
    }

    public void setConcerts(List<Concert> concerts) {
        this.concerts = concerts;
    }
}
