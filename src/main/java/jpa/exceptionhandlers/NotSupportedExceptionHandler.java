package jpa.exceptionhandlers;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception handler NotSupportedExceptionHandler.
 */
@Provider
public class NotSupportedExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<NotSupportedException> {

    /**
     * Executes toResponse operation.
     *
     * @param ex method parameter
     * @return operation result
     */
    @Override
    public Response toResponse(NotSupportedException ex) {
        return buildResponse(
                Response.Status.UNSUPPORTED_MEDIA_TYPE,
                safeMessage(ex, "Unsupported media type")
        );
    }
}
