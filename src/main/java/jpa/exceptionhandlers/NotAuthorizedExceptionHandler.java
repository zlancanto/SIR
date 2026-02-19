package jpa.exceptionhandlers;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception handler NotAuthorizedExceptionHandler.
 */
@Provider
public class NotAuthorizedExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<NotAuthorizedException> {

    /**
     * Executes toResponse operation.
     *
     * @param ex method parameter
     * @return operation result
     */
    @Override
    public Response toResponse(NotAuthorizedException ex) {
        return buildResponse(
                Response.Status.UNAUTHORIZED,
                safeMessage(ex, "Unauthorized")
        );
    }
}
