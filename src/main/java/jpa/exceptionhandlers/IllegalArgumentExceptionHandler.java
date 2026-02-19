package jpa.exceptionhandlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return buildResponse(
                Response.Status.BAD_REQUEST,
                safeMessage(exception, "Invalid request")
        );
    }
}
