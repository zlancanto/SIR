package jpa.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jpa.security.PasswordHasher;

/**
 * JPA entity User.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
public abstract class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    /**
     * Executes getEmail operation.
     *
     * @return operation result
     */
    public String getEmail() {
        return email;
    }

    /**
     * Executes setEmail operation.
     *
     * @param email method parameter
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Executes getPassword operation.
     *
     * @return operation result
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }

    /**
     * Executes setPassword operation.
     *
     * @param rawPassword method parameter
     */
    public void setPassword(String rawPassword) {
        this.password = PasswordHasher.hash(rawPassword);
    }

    /**
     * Executes verifyPassword operation.
     *
     * @param rawPassword method parameter
     * @return operation result
     */
    public boolean verifyPassword(String rawPassword) {
        return PasswordHasher.matches(rawPassword, password);
    }

    /**
     * Executes getFirstName operation.
     *
     * @return operation result
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Executes setFirstName operation.
     *
     * @param firstName method parameter
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Executes getLastName operation.
     *
     * @return operation result
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Executes setLastName operation.
     *
     * @param lastName method parameter
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
