package jpa.exceptionhandlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalStateExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<IllegalStateException> {

    @Override
    public Response toResponse(IllegalStateException ex) {
        return buildResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "Internal server error"
        );
    }
}
