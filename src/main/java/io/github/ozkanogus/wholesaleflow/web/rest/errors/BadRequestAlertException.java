package io.github.ozkanogus.wholesaleflow.web.rest.errors;

public class BadRequestAlertException extends RuntimeException {
    public BadRequestAlertException(String defaultMessage) {
        super(defaultMessage);
    }
}
