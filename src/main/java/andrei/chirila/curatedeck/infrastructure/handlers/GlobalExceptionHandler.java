package andrei.chirila.curatedeck.infrastructure.handlers;

import andrei.chirila.curatedeck.domain.exceptions.ElErrorMessage;
import andrei.chirila.curatedeck.domain.exceptions.ElException;
import andrei.chirila.curatedeck.infrastructure.dtos.ApiElError;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(basePackages = "andrei.chirila.prove_yourself.infrastructure.controllers")
public class GlobalExceptionHandler {


    @ExceptionHandler(ElException.class)
    public String handleElException(ElException ex, HttpServletResponse response) {
        final ElErrorMessage errorMessage = ex.getErrorMessage();

        switch (errorMessage) {
            case USER_NOT_FOUND -> {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return "fragments/notifications :: user-not-found";
            }

            case URL_DOES_NOT_MATCH_PATTERN -> {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "fragments/notifications :: url-pattern-mismatch";
            }

            case PROJECT_NOT_FOUND -> {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return "fragments/notifications :: project-not-found";
            }

            case PROJECT_START_END_DATES_MISMATCH -> {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "fragments/notifications :: project-dates-mismatch";
            }

            case INTERNAL_SERVER_ERROR -> {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return "fragments/notifications :: internal-server-error";
            }

            case BAD_REQUEST -> {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "fragments/notifications :: bad-request";
            }

            default -> {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "fragments/notifications :: default";
            }
        }
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiElError> handleExceptions(MethodArgumentTypeMismatchException ex) {
        final ElErrorMessage errorMessage = ElErrorMessage.BAD_REQUEST;
        final HttpStatus status = ElErrorMessage.getHttpStatus(errorMessage);

        final String paramName = ex.getName();

        String requiredTypeName = "unknown";
        if (ex.getRequiredType() != null) {
            requiredTypeName = ex.getRequiredType().getSimpleName();

        }

        String providerTypeName = "unknown";
        if (ex.getValue() != null) {
            providerTypeName = ex.getValue().getClass().getSimpleName();
        }

        final String message = String.format(
                "Invalid value for parameter '%s'. Expected type: '%s', but got '%s'.",
                paramName, requiredTypeName, providerTypeName);

        final ApiElError body = createApiElError(errorMessage, message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiElError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    final String fieldName = error.getField();
                    final String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        final ElErrorMessage errorMessage = ElErrorMessage.BAD_REQUEST;
        final HttpStatus status = ElErrorMessage.getHttpStatus(errorMessage);
        final ApiElError body = new ApiElError(errorMessage, errors.toString());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiElError> handleGenericException(Exception ex) {
        final ElErrorMessage errorMessage = ElErrorMessage.INTERNAL_SERVER_ERROR;
        final HttpStatus status = ElErrorMessage.getHttpStatus(errorMessage);
        final ApiElError body = createApiElError(errorMessage);

        return ResponseEntity.status(status).body(body);

    }

    private ApiElError createApiElError(ElErrorMessage errorMessage) {
        final String message = ElErrorMessage.getDescription(errorMessage);

        return new ApiElError(errorMessage, message);
    }

    private ApiElError createApiElError(ElErrorMessage errorMessage, String message) {
        return new ApiElError(errorMessage, message);
    }
}
