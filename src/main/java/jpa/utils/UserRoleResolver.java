package jpa.utils;

import jpa.entities.Admin;
import jpa.entities.Customer;
import jpa.entities.Organizer;
import jpa.entities.User;
import jpa.enums.Roles;
import org.hibernate.Hibernate;

/**
 * Utility resolver converting persisted user subtype into the canonical role enum.
 */
public final class UserRoleResolver {
    private UserRoleResolver() {}

    /**
     * Resolves role from a persisted user subtype.
     *
     * @param user persisted user entity
     * @return matching role enum
     */
    public static Roles resolve(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        Object resolved = Hibernate.unproxy(user);
        if (!(resolved instanceof User resolvedUser)) {
            throw new IllegalStateException("Unsupported user proxy type: " + resolved.getClass().getName());
        }

        if (resolvedUser instanceof Admin) {
            return Roles.ROLE_ADMIN;
        }
        if (resolvedUser instanceof Organizer) {
            return Roles.ROLE_ORGANIZER;
        }
        if (resolvedUser instanceof Customer) {
            return Roles.ROLE_CUSTOMER;
        }
        throw new IllegalStateException("Unsupported user type: " + resolvedUser.getClass().getName());
    }
}
