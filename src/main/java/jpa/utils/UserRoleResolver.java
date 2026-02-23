package jpa.utils;

import jpa.entities.Admin;
import jpa.entities.Customer;
import jpa.entities.Organizer;
import jpa.entities.User;
import jpa.enums.Roles;

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
        if (user instanceof Admin) {
            return Roles.ROLE_ADMIN;
        }
        if (user instanceof Organizer) {
            return Roles.ROLE_ORGANIZER;
        }
        if (user instanceof Customer) {
            return Roles.ROLE_CUSTOMER;
        }
        throw new IllegalStateException("Unsupported user type: " + user.getClass().getSimpleName());
    }
}
