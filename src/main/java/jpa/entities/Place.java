package jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Venue reference entity used by concerts.
 */
@Entity
@Table(name = "places")
public class Place extends BaseEntity {

    private String name;
    private String address;
    private Integer zipCode;
    private String city;
    private Integer capacity;

    /**
     * Returns the venue name.
     *
     * @return venue name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the venue name.
     *
     * @param name venue name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the street address of the venue.
     *
     * @return street address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the street address of the venue.
     *
     * @param address street address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the postal code of the venue.
     *
     * @return postal code
     */
    public Integer getZipCode() {
        return zipCode;
    }

    /**
     * Sets the postal code of the venue.
     *
     * @param zipCode postal code
     */
    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Returns the city where the venue is located.
     *
     * @return city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city where the venue is located.
     *
     * @param city city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the maximum audience capacity of the venue.
     *
     * @return maximum number of seats
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * Sets the maximum audience capacity of the venue.
     *
     * @param capacity maximum number of seats
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

}
