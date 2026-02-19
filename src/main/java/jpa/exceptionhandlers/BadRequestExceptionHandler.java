package jpa.exceptionhandlers;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionHandler
        extends BaseExceptionMapper
        implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException ex) {
        return buildResponse(
                Response.Status.BAD_REQUEST,
                safeMessage(ex, "Bad Request")
        );
    }
}
