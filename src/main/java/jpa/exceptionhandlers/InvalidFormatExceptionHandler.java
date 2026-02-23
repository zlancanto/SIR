package jpa.exceptionhandlers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.UUID;

/**
 * Exception handler for JSON values with invalid formats.
 */
@Provider
public class InvalidFormatExceptionHandler extends BaseExceptionMapper
        implements ExceptionMapper<InvalidFormatException> {

    /**
     * Executes toResponse operation.
     *
     * @param ex method parameter
     * @return operation result
     */
    @Override
    public Response toResponse(InvalidFormatException ex) {
        return buildResponse(
                Response.Status.BAD_REQUEST,
                buildInvalidFormatMessage(ex)
        );
    }

    private String buildInvalidFormatMessage(InvalidFormatException ex) {
        String field = "request body";
        if (ex.getPath() != null && !ex.getPath().isEmpty()) {
            JsonMappingException.Reference ref = ex.getPath().get(ex.getPath().size() - 1);
            if (ref.getFieldName() != null && !ref.getFieldName().isBlank()) {
                field = ref.getFieldName();
            } else if (ref.getIndex() >= 0) {
                field = "index " + ref.getIndex();
            }
        }

        if (UUID.class.equals(ex.getTargetType())) {
            return "Invalid value for '" + field
                    + "': expected UUID format xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
        }

        return "Invalid value for '" + field + "'";
    }
}
