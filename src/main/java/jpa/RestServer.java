package jpa;

import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import java.util.logging.Logger;

/**
 * Application bootstrap server.
 */
public class RestServer {

    private static final Logger logger = Logger.getLogger(RestServer.class.getName());

    /**
     * Executes main operation.
     *
     * @param args method parameter
     */
    public static void main(String[] args) {
        UndertowJaxrsServer ut = new UndertowJaxrsServer();
        TestApplication ta = new TestApplication();

        // Deploy JAX-RS resources and providers before opening the HTTP listener.
        ut.deploy(ta);
        ut.start(Undertow.builder()
                .addHttpListener(8081, "localhost")
        );

        logger.info("JAX-RS based micro-service running!");
    }
}
