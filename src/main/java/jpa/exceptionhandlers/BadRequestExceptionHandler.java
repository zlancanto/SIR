package jpa.exceptionhandlers;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception handler BadRequestExceptionHandler.
 */
@Provider
public class BadRequestExceptionHandler
        extends BaseExceptionMapper
        implements ExceptionMapper<BadRequestException> {

    /**
     * Executes toResponse operation.
     *
     * @param ex method parameter
     * @return operation result
     */
    @Override
    public Response toResponse(BadRequestException ex) {
        return buildResponse(
                Response.Status.BAD_REQUEST,
                safeMessage(ex, "Bad Request")
        );
    }
}
