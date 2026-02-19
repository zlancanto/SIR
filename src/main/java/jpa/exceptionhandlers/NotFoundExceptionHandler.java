package jpa.exceptionhandlers;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException ex) {
        return buildResponse(
                Response.Status.NOT_FOUND,
                safeMessage(ex, "Resource not found")
        );
    }
}
