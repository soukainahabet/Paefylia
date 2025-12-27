package parfumerie.parfilya.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", "Erreur de validation");
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> {
                Map<String, String> fieldError = new HashMap<>();
                fieldError.put("field", error.getField());
                fieldError.put("message", error.getDefaultMessage());
                return fieldError;
            })
            .collect(Collectors.toList());

        errorResponse.put("errors", fieldErrors);

        logger.warn("Validation failed: {}", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Email already exists") ||
                ex.getMessage().contains("already exists")) {
                status = HttpStatus.CONFLICT;
                logger.warn("Conflit: {}", ex.getMessage());
            } else if (ex.getMessage().contains("Invalid credentials") ||
                       ex.getMessage().contains("not found")) {
                status = HttpStatus.UNAUTHORIZED;
                logger.warn("Authentification échouée: {}", ex.getMessage());
            } else if (ex.getMessage().contains("Forbidden") ||
                       ex.getMessage().contains("Access denied")) {
                status = HttpStatus.FORBIDDEN;
                logger.warn("Accès refusé: {}", ex.getMessage());
            } else {
                logger.error("Erreur runtime: {}", ex.getMessage());
            }
        }

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", "Une erreur interne s'est produite");
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        logger.error("Erreur interne: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}











