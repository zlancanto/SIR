package jpa;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jpa.controllers.UserController;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class TestApplication extends Application {


    @Override
    public Set<Class<?>> getClasses() {

        final Set<Class<?>> clazzes = new HashSet<>();

        clazzes.add(OpenApiResource.class);
        clazzes.add(UserController.class);

        return clazzes;
    }

}
