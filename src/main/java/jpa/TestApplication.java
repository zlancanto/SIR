package jpa;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jpa.config.JacksonObjectMapperProvider;
import jpa.controllers.UserController;
import jpa.exceptionhandlers.*;

import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS application registration class.
 */
@ApplicationPath("/")
public class TestApplication extends Application {


    @Override
    public Set<Class<?>> getClasses() {

        final Set<Class<?>> clazzes = new HashSet<>();

        clazzes.add(OpenApiResource.class);
        clazzes.add(UserController.class);

        clazzes.add(JacksonObjectMapperProvider.class);

        clazzes.add(BadRequestExceptionHandler.class);
        clazzes.add(ForbiddenExceptionHandler.class);
        clazzes.add(NotFoundExceptionHandler.class);
        clazzes.add(NotAuthorizedExceptionHandler.class);
        clazzes.add(NotAllowedExceptionHandler.class);
        clazzes.add(NotSupportedExceptionHandler.class);
        clazzes.add(ClientErrorExceptionHandler.class);
        clazzes.add(ServerErrorExceptionHandler.class);
        clazzes.add(IllegalArgumentExceptionHandler.class);
        clazzes.add(IllegalStateExceptionHandler.class);
        clazzes.add(WebApplicationExceptionHandler.class);
        clazzes.add(GenericExceptionHandler.class);

        return clazzes;
    }

}
