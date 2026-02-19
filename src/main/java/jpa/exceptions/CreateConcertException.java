package jpa.exceptions;

import java.io.Serial;

public class CreateConcertException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4194839856870167961L;
    public CreateConcertException(String message) {
        super(message);
    }
}
