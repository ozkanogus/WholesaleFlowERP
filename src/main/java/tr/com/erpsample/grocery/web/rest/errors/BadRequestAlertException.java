package tr.com.erpsample.grocery.web.rest.errors;

public class BadRequestAlertException extends RuntimeException {
    public BadRequestAlertException(String defaultMessage) {
        super(defaultMessage);
    }
}
