package jpa.exceptionhandlers;

import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception handler NotAllowedExceptionHandler.
 */
@Provider
public class NotAllowedExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<NotAllowedException> {

    /**
     * Executes toResponse operation.
     *
     * @param ex method parameter
     * @return operation result
     */
    @Override
    public Response toResponse(NotAllowedException ex) {
        return buildResponse(
                Response.Status.METHOD_NOT_ALLOWED,
                safeMessage(ex, "Method not allowed")
        );
    }
}
