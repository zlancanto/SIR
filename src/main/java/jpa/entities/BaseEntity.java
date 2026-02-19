package jpa.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity BaseEntity.
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    protected Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * Executes getId operation.
     *
     * @return operation result
     */
    public UUID getId() {
        return id;
    }

    /**
     * Executes setId operation.
     *
     * @param id method parameter
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Executes getCreatedAt operation.
     *
     * @return operation result
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Executes setCreatedAt operation.
     *
     * @param createdAt method parameter
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Executes getUpdatedAt operation.
     *
     * @return operation result
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Executes setUpdatedAt operation.
     *
     * @param updatedAt method parameter
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
