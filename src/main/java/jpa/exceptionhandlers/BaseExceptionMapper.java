package jpa.exceptionhandlers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jpa.dto.exceptions.ResponseExceptionDto;

import java.time.Instant;

public abstract class BaseExceptionMapper {

    @Context
    protected UriInfo uriInfo;

    protected Response buildResponse(Response.StatusType status, String message) {
        String path = "";
        if (uriInfo != null && uriInfo.getRequestUri() != null) {
            path = uriInfo.getRequestUri().getPath();
        }

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

    protected String safeMessage(Throwable ex, String fallback) {
        String message = ex.getMessage();
        return (message == null || message.isBlank()) ? fallback : message;
    }
}
