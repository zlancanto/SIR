package jpa.config;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import jpa.dao.generic.EntityManagerHelper;

/**
 * Ensures one clean EntityManager context per HTTP request thread.
 */
@Provider
@Priority(Priorities.USER)
public class EntityManagerPerRequestFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Clears any stale thread-bound EntityManager before request processing.
     *
     * @param requestContext request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        EntityManagerHelper.closeEntityManager();
    }

    /**
     * Closes the EntityManager after response writing.
     *
     * @param requestContext request context
     * @param responseContext response context
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        EntityManagerHelper.closeEntityManager();
    }
}
