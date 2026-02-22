package jpa.utils;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.SecurityContext;

public class Security {
    private Security() {}

    public static String resolveAuthenticatedEmail(SecurityContext securityContext, String msgException) {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            throw new ForbiddenException(msgException);
        }

        String principalName = securityContext.getUserPrincipal().getName();
        if (principalName == null || principalName.isBlank()) {
            throw new ForbiddenException(msgException);
        }

        return principalName.trim();
    }
}
