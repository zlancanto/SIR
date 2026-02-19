package jpa.exceptionhandlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exception handler GenericExceptionHandler.
 */
@Provider
public class GenericExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionHandler.class.getName());

    /**
     * Executes toResponse operation.
     *
     * @param exception method parameter
     * @return operation result
     */
    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.log(Level.SEVERE, "Unhandled exception", exception);
        return buildResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }
}
