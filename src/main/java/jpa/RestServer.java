package jpa;

import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import jpa.config.ReferenceDataInitializer;

import java.util.logging.Logger;

/**
 * Application bootstrap server.
 */
public class RestServer {

    private static final int PORT = 8081;
    private static final String HOST = "localhost";
    private static final Logger logger = Logger.getLogger(RestServer.class.getName());

    /**
     * Executes main operation.
     *
     * @param args method parameter
     */
    public static void main(String[] args) {
        ReferenceDataInitializer.seedPlacesIfEmpty();
        ReferenceDataInitializer.seedConcertsIfEmpty();

        UndertowJaxrsServer ut = new UndertowJaxrsServer();
        TestApplication ta = new TestApplication();

        // Deploy JAX-RS resources and providers before opening the HTTP listener.
        ut.deploy(ta);
        ut.start(Undertow.builder().addHttpListener(PORT, HOST));

        logger.info("JAX-RS based micro-service running!");
    }
}
