package jpa.exceptionhandlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception handler GenericExceptionHandler.
 */
@Provider
public class GenericExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<Throwable> {

    /**
     * Executes toResponse operation.
     *
     * @param exception method parameter
     * @return operation result
     */
    @Override
    public Response toResponse(Throwable exception) {
        return buildResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                exception
        );
    }
}
