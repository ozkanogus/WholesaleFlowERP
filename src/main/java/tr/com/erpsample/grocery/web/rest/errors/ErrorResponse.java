package tr.com.erpsample.grocery.web.rest.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/** Application-owned compatibility format; absent optional fields stay absent. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String type, String title, int status, String detail,
                            List<Violation> violations) {
    public record Violation(String field, String message) {}
}
