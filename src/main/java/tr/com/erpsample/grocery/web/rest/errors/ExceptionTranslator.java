package tr.com.erpsample.grocery.web.rest.errors;

import jakarta.validation.ConstraintViolationException;
import java.net.SocketTimeoutException;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Spring MVC classification with the application's existing JSON error contract. */
@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestAlertException exception) {
        return response(new ErrorResponse(null, exception.getMessage(), 400, null, null),
            new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return validation(exception.getBindingResult(), headers);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBinding(BindException exception) {
        return validation(exception.getBindingResult(), new HttpHeaders());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception) {
        return violations(exception.getConstraintViolations().stream()
            .map(v -> new ErrorResponse.Violation(v.getPropertyPath().toString(), v.getMessage()))
            .toList(), new HttpHeaders());
    }

    private ResponseEntity<Object> validation(BindingResult result, HttpHeaders headers) {
        return violations(result.getAllErrors().stream()
            .map(error -> new ErrorResponse.Violation(
                error instanceof FieldError field ? field.getField() : error.getObjectName(),
                error.getDefaultMessage())).toList(), headers);
    }

    private ResponseEntity<Object> violations(List<ErrorResponse.Violation> values, HttpHeaders headers) {
        var sorted = values.stream().sorted(Comparator.comparing(ErrorResponse.Violation::field)
            .thenComparing(ErrorResponse.Violation::message)).toList();
        // Retained URI is a wire-contract identifier, not a library dependency.
        return response(new ErrorResponse("https://zalando.github.io/problem/constraint-violation",
            "Constraint Violation", 400, null, sorted), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipart(MultipartException exception) {
        return error(exception, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<Object> handleSocketTimeout(SocketTimeoutException exception) {
        return error(exception, new HttpHeaders(), HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Object> handleUnsupportedOperation(UnsupportedOperationException exception) {
        return error(exception, new HttpHeaders(), HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception exception) {
        // Do not log arbitrary exception messages, request bodies or credentials.
        logger.error("Unexpected MVC failure (" + exception.getClass().getName() + ")");
        return response(new ErrorResponse(null, "Internal Server Error", 500, null, null),
            new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        // These subclasses had 400 mappings in the former general binding/IO advice.
        if (exception instanceof org.springframework.web.bind.MissingPathVariableException
                || exception instanceof org.springframework.beans.ConversionNotSupportedException
                || exception instanceof org.springframework.web.multipart.MaxUploadSizeExceededException) {
            status = HttpStatus.BAD_REQUEST;
        }
        // Let Spring retain committed-response handling and framework request attributes.
        return super.handleExceptionInternal(exception, problem(exception, status), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return response(body, headers, status);
    }

    private ResponseEntity<Object> error(Exception exception, HttpHeaders headers, HttpStatusCode status) {
        return response(problem(exception, status), headers, status);
    }

    private ErrorResponse problem(Exception exception, HttpStatusCode status) {
        var knownStatus = HttpStatus.resolve(status.value());
        String title = knownStatus == null ? "HTTP Error" : knownStatus.getReasonPhrase();
        String detail = status.value() == 500 ? null : exception.getMessage();
        return new ErrorResponse(null, title, status.value(), detail, null);
    }

    private ResponseEntity<Object> response(Object body, HttpHeaders headers, HttpStatusCode status) {
        var responseHeaders = new HttpHeaders();
        responseHeaders.putAll(headers);
        responseHeaders.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return new ResponseEntity<>(body, responseHeaders, status);
    }
}
