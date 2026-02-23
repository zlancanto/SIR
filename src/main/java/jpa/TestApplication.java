package jpa;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jpa.config.JacksonObjectMapperProvider;
import jpa.controllers.AuthController;
import jpa.controllers.ConcertController;
import jpa.controllers.OpenApiAliasController;
import jpa.controllers.SwaggerUiController;
import jpa.controllers.TicketController;
import jpa.controllers.UserController;
import jpa.exceptionhandlers.*;
import jpa.security.JwtAuthorizationFilter;
import org.jboss.resteasy.plugins.interceptors.RoleBasedSecurityFeature;

import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS application registration class.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "SIR Concert API",
                version = "1.0.0",
                description = "API for user registration, concert creation, moderation and publication."
        )
)
@ApplicationPath("/")
public class TestApplication extends Application {


    @Override
    public Set<Class<?>> getClasses() {

        final Set<Class<?>> clazzes = new HashSet<>();

        clazzes.add(OpenApiResource.class);
        clazzes.add(AuthController.class);
        clazzes.add(UserController.class);
        clazzes.add(ConcertController.class);
        clazzes.add(TicketController.class);
        clazzes.add(OpenApiAliasController.class);
        clazzes.add(SwaggerUiController.class);
        clazzes.add(JwtAuthorizationFilter.class);
        clazzes.add(RoleBasedSecurityFeature.class);

        clazzes.add(JacksonObjectMapperProvider.class);

        clazzes.add(BadRequestExceptionHandler.class);
        clazzes.add(ForbiddenExceptionHandler.class);
        clazzes.add(NotFoundExceptionHandler.class);
        clazzes.add(NotAuthorizedExceptionHandler.class);
        clazzes.add(NotAllowedExceptionHandler.class);
        clazzes.add(NotSupportedExceptionHandler.class);
        clazzes.add(ClientErrorExceptionHandler.class);
        clazzes.add(ServerErrorExceptionHandler.class);
        clazzes.add(InvalidFormatExceptionHandler.class);
        clazzes.add(IllegalArgumentExceptionHandler.class);
        clazzes.add(IllegalStateExceptionHandler.class);
        clazzes.add(WebApplicationExceptionHandler.class);
        clazzes.add(GenericExceptionHandler.class);

        return clazzes;
    }

}
