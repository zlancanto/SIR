package jpa.config;

import static jpa.utils.StringValidation.firstNonBlank;

/**
 * Ticket-related configuration keys and resolvers.
 */
public final class TicketConfig {
    public static final String MAX_TICKET_BATCH_SIZE_PROPERTY = "app.ticket.max-batch-size";
    public static final String MAX_TICKET_BATCH_SIZE_ENV = "APP_TICKET_MAX_BATCH_SIZE";
    private static final int DEFAULT_MAX_TICKET_BATCH_SIZE = 10_000;

    private TicketConfig() {}

    /**
     * Resolves the maximum number of tickets that can be created in one batch.
     *
     * @return configured maximum batch size, or default when unspecified
     */
    public static int resolveMaxTicketBatchSize() {
        String rawValue = firstNonBlank(
                System.getProperty(MAX_TICKET_BATCH_SIZE_PROPERTY),
                System.getenv(MAX_TICKET_BATCH_SIZE_ENV)
        );

        if (rawValue == null) {
            return DEFAULT_MAX_TICKET_BATCH_SIZE;
        }

        try {
            int parsed = Integer.parseInt(rawValue.trim());
            if (parsed <= 0) {
                throw new IllegalStateException("Ticket max batch size must be greater than 0");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Ticket max batch size must be a valid integer", ex);
        }
    }
}
