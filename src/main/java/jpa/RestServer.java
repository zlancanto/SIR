package jpa;

import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import jpa.config.ReferenceDataInitializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
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
        configureLogging();

        ReferenceDataInitializer.seedPlacesIfEmpty();
        ReferenceDataInitializer.seedConcertsIfEmpty();

        UndertowJaxrsServer ut = new UndertowJaxrsServer();
        TestApplication ta = new TestApplication();

        // Deploy JAX-RS resources and providers before opening the HTTP listener.
        ut.deploy(ta);
        ut.start(Undertow.builder().addHttpListener(PORT, HOST));

        logger.info("JAX-RS based micro-service running!");
    }

    private static void configureLogging() {
        try (InputStream config = RestServer.class.getClassLoader().getResourceAsStream("logging.properties")) {
            if (config == null) {
                logger.warning("logging.properties not found on classpath; default JUL configuration will be used.");
                return;
            }
            LogManager.getLogManager().readConfiguration(config);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Failed to load logging.properties; default JUL configuration will be used.", ex);
        }
    }
}
