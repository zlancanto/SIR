package jpa.config;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * Adds CORS headers and handles preflight requests for browser clients.
 */
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION - 100)
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String ORIGIN_HEADER = "Origin";
    private static final String ALLOWED_ORIGIN = CorsConfig.resolveAllowedOrigin();
    private static final String ALLOWED_METHODS = "GET,POST,PUT,PATCH,DELETE,OPTIONS";
    private static final String ALLOWED_HEADERS = "Content-Type,Authorization,X-Admin-Registration-Key";
    private static final String EXPOSED_HEADERS = "Location";
    private static final String MAX_AGE_SECONDS = "3600";

    /**
     * Short-circuits preflight requests before resource matching.
     *
     * @param requestContext current request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!"OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        String origin = requestContext.getHeaderString(ORIGIN_HEADER);
        if (!isAllowedOrigin(origin)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
            return;
        }

        requestContext.abortWith(
                Response.ok()
                        .header("Access-Control-Allow-Origin", origin)
                        .header("Vary", ORIGIN_HEADER)
                        .header("Access-Control-Allow-Methods", ALLOWED_METHODS)
                        .header("Access-Control-Allow-Headers", ALLOWED_HEADERS)
                        .header("Access-Control-Expose-Headers", EXPOSED_HEADERS)
                        .header("Access-Control-Max-Age", MAX_AGE_SECONDS)
                        .build()
        );
    }

    /**
     * Decorates normal responses with CORS headers.
     *
     * @param requestContext current request context
     * @param responseContext current response context
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String origin = requestContext.getHeaderString(ORIGIN_HEADER);
        if (!isAllowedOrigin(origin)) {
            return;
        }

        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.putSingle("Access-Control-Allow-Origin", origin);
        headers.putSingle("Vary", ORIGIN_HEADER);
        headers.putSingle("Access-Control-Allow-Methods", ALLOWED_METHODS);
        headers.putSingle("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        headers.putSingle("Access-Control-Expose-Headers", EXPOSED_HEADERS);
        headers.putSingle("Access-Control-Max-Age", MAX_AGE_SECONDS);
    }

    /**
     * Validates request origin against configured allowed origin.
     *
     * @param origin request origin header
     * @return {@code true} when allowed
     */
    private boolean isAllowedOrigin(String origin) {
        if (origin == null || origin.isBlank()) {
            return false;
        }

        return ALLOWED_ORIGIN.equalsIgnoreCase(origin.trim());
    }
}
