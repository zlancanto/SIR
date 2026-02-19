package jpa.exceptionhandlers;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception handler ClientErrorExceptionHandler.
 */
@Provider
public class ClientErrorExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<ClientErrorException> {

    /**
     * Executes toResponse operation.
     *
     * @param ex method parameter
     * @return operation result
     */
    @Override
    public Response toResponse(ClientErrorException ex) {
        Response.StatusType status = ex.getResponse() != null
                ? ex.getResponse().getStatusInfo()
                : Response.Status.BAD_REQUEST;

        return buildResponse(
                status,
                safeMessage(ex, status.getReasonPhrase())
        );
    }
}
