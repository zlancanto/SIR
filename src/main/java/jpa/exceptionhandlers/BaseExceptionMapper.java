package jpa.exceptionhandlers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jpa.dto.exceptions.ResponseExceptionDto;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exception handler BaseExceptionMapper.
 */
public abstract class BaseExceptionMapper {

    private static final Logger LOGGER = Logger.getLogger(BaseExceptionMapper.class.getName());

    @Context
    protected UriInfo uriInfo;

    protected Response buildResponse(Response.StatusType status, String message) {
        return buildResponse(status, message, null);
    }

    protected Response buildResponse(Response.StatusType status, String message, Throwable ex) {
        String path = resolvePath();
        if (ex != null) {
            Level level = status.getStatusCode() >= 500 ? Level.SEVERE : Level.WARNING;
            LOGGER.log(
                    level,
                    "Request failed with status " + status.getStatusCode() + " ("
                            + status.getReasonPhrase() + ") on path '" + path + "': " + message,
                    ex
            );
        }

        // Keep a uniform error payload for all exception mappers.
        ResponseExceptionDto payload = new ResponseExceptionDto(
                Instant.now(),
                status.getStatusCode(),
                status.getReasonPhrase(),
                message,
                path
        );

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(payload)
                .build();
    }

    private String resolvePath() {
        if (uriInfo != null && uriInfo.getRequestUri() != null) {
            return uriInfo.getRequestUri().getPath();
        }
        return "";
    }

    protected String safeMessage(Throwable ex, String fallback) {
        String message = ex.getMessage();
        return (message == null || message.isBlank()) ? fallback : message;
    }
}
