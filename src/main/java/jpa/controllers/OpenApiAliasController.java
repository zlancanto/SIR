package jpa.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;

/**
 * Compatibility endpoint to redirect /openapi to /openapi.json.
 */
@Path("/openapi")
public class OpenApiAliasController {

    /**
     * Redirects to the OpenAPI JSON endpoint exposed by swagger-jaxrs2.
     *
     * @param uriInfo request URI context
     * @return HTTP 307 temporary redirect to /openapi.json
     */
    @GET
    public Response redirectToJson(@Context UriInfo uriInfo) {
        return Response.temporaryRedirect(
                UriBuilder.fromUri(uriInfo.getBaseUri()).path("openapi.json").build()
        ).build();
    }
}
