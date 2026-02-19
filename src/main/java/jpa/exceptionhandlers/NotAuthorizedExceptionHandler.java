package jpa.exceptionhandlers;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<NotAuthorizedException> {

    @Override
    public Response toResponse(NotAuthorizedException ex) {
        return buildResponse(
                Response.Status.UNAUTHORIZED,
                safeMessage(ex, "Unauthorized")
        );
    }
}
