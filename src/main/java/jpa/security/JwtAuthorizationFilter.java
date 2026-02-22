package jpa.security;

import jakarta.annotation.Priority;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import jpa.config.Instance;
import jpa.dto.security.AccessTokenClaimsDto;
import jpa.security.interfaces.AccessTokenService;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.security.Principal;

/**
 * Authentication filter responsible for extracting and validating Bearer access tokens.
 *
 * <p>The filter sets a request {@link SecurityContext} when a valid token is provided.
 * For endpoints protected by {@link RolesAllowed}, a missing or invalid token results in
 * a {@link NotAuthorizedException}.</p>
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthorizationFilter implements ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AccessTokenService accessTokenService;

    @Context
    private ResourceInfo resourceInfo;

    /**
     * Builds the filter with application-wide token service wiring.
     */
    public JwtAuthorizationFilter() {
        this.accessTokenService = Instance.ACCESS_TOKEN_SERVICE;
    }

    /**
     * Validates incoming bearer tokens and sets the request security context.
     *
     * @param requestContext current request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        boolean authenticationRequired = isAuthenticationRequired();
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            if (authenticationRequired) {
                throw new NotAuthorizedException("Missing bearer token");
            }

            requestContext.setSecurityContext(new AnonymousSecurityContext(requestContext));
            return;
        }

        if (!authorizationHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            if (authenticationRequired) {
                throw new NotAuthorizedException("Authorization header must use Bearer scheme");
            }

            requestContext.setSecurityContext(new AnonymousSecurityContext(requestContext));
            return;
        }

        String rawToken = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        try {
            AccessTokenClaimsDto claims = accessTokenService.verifyAccessToken(rawToken);
            requestContext.setSecurityContext(new JwtSecurityContext(claims, requestContext));
        } catch (NotAuthorizedException ex) {
            if (authenticationRequired) {
                throw ex;
            }

            requestContext.setSecurityContext(new AnonymousSecurityContext(requestContext));
        }
    }

    /**
     * Resolves whether the current endpoint requires authentication based on role annotations.
     *
     * @return {@code true} when {@link RolesAllowed} is present and not overridden by {@link PermitAll}
     */
    private boolean isAuthenticationRequired() {
        Method resourceMethod = resourceInfo != null ? resourceInfo.getResourceMethod() : null;
        Class<?> resourceClass = resourceInfo != null ? resourceInfo.getResourceClass() : null;

        if (hasAnnotation(resourceMethod, PermitAll.class)) {
            return false;
        }
        if (hasAnnotation(resourceMethod, RolesAllowed.class)) {
            return true;
        }
        if (hasAnnotation(resourceClass, PermitAll.class)) {
            return false;
        }

        return hasAnnotation(resourceClass, RolesAllowed.class);
    }

    /**
     * Checks whether a given annotated element contains the specified annotation type.
     *
     * @param element element to inspect
     * @param annotationClass annotation type to search
     * @return {@code true} when annotation is present
     */
    private boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
        if (element == null) {
            return false;
        }

        return element.isAnnotationPresent(annotationClass);
    }

    /**
     * Anonymous security context used for public endpoints without credentials.
     */
    private static final class AnonymousSecurityContext implements SecurityContext {

        private final boolean secure;

        /**
         * Creates a context from the current request.
         *
         * @param requestContext current request context
         */
        private AnonymousSecurityContext(ContainerRequestContext requestContext) {
            SecurityContext base = requestContext.getSecurityContext();
            this.secure = base != null && base.isSecure();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSecure() {
            return secure;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getAuthenticationScheme() {
            return null;
        }
    }

    /**
     * Authenticated security context backed by validated JWT claims.
     */
    private static final class JwtSecurityContext implements SecurityContext {

        private final AccessTokenClaimsDto claims;
        private final boolean secure;

        /**
         * Creates a security context bound to current claims.
         *
         * @param claims validated token claims
         * @param requestContext current request context
         */
        private JwtSecurityContext(AccessTokenClaimsDto claims, ContainerRequestContext requestContext) {
            this.claims = claims;
            SecurityContext base = requestContext.getSecurityContext();
            this.secure = base != null && base.isSecure();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Principal getUserPrincipal() {
            return claims::email;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isUserInRole(String role) {
            return role != null && role.equals(claims.role());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSecure() {
            return secure;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }
}
