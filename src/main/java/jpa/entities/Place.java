package jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * JPA entity Place.
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
     * Executes getName operation.
     *
     * @return operation result
     */
    public String getName() {
        return name;
    }

    /**
     * Executes setName operation.
     *
     * @param name method parameter
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Executes getAddress operation.
     *
     * @return operation result
     */
    public String getAddress() {
        return address;
    }

    /**
     * Executes setAddress operation.
     *
     * @param address method parameter
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Executes getZipCode operation.
     *
     * @return operation result
     */
    public Integer getZipCode() {
        return zipCode;
    }

    /**
     * Executes setZipCode operation.
     *
     * @param zipCode method parameter
     */
    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Executes getCity operation.
     *
     * @return operation result
     */
    public String getCity() {
        return city;
    }

    /**
     * Executes setCity operation.
     *
     * @param city method parameter
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Executes getCapacity operation.
     *
     * @return operation result
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * Executes setCapacity operation.
     *
     * @param capacity method parameter
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

}
