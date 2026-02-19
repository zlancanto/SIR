package jpa.exceptionhandlers;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response.StatusType status = exception.getResponse() != null
                ? exception.getResponse().getStatusInfo()
                : Response.Status.INTERNAL_SERVER_ERROR;

        String message = status.getStatusCode() >= 500
                ? "Internal server error"
                : safeMessage(exception, status.getReasonPhrase());

        return buildResponse(status, message);
    }
}
