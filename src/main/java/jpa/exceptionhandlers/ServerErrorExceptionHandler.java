package jpa.exceptionhandlers;

import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ServerErrorExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<ServerErrorException> {

    @Override
    public Response toResponse(ServerErrorException ex) {
        Response.StatusType status = ex.getResponse() != null
                ? ex.getResponse().getStatusInfo()
                : Response.Status.INTERNAL_SERVER_ERROR;

        String message = status.getStatusCode() >= 500
                ? "Internal server error"
                : safeMessage(ex, status.getReasonPhrase());

        return buildResponse(status, message);
    }
}
