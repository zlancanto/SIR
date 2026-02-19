package jpa.exceptionhandlers;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception handler ForbiddenExceptionHandler.
 */
@Provider
public class ForbiddenExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<ForbiddenException> {

    /**
     * Executes toResponse operation.
     *
     * @param ex method parameter
     * @return operation result
     */
    @Override
    public Response toResponse(ForbiddenException ex) {
        return buildResponse(
                Response.Status.FORBIDDEN,
                safeMessage(ex, "Forbidden")
        );
    }
}
