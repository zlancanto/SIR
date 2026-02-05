package jpa.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    @OneToMany(mappedBy = "admin")
    private List<Concert> concerts = new ArrayList<>();

    public List<Concert> getConcerts() {
        return concerts;
    }

    public void setConcerts(List<Concert> concerts) {
        this.concerts = concerts;
    }
}
