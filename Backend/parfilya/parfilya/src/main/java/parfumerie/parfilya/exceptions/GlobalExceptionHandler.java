package parfumerie.parfilya.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", ex.getMessage());
        
        // Déterminer le status code selon le message
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Email already exists") || 
                ex.getMessage().contains("already exists")) {
                status = HttpStatus.CONFLICT; // 409
            } else if (ex.getMessage().contains("Invalid credentials") ||
                       ex.getMessage().contains("not found")) {
                status = HttpStatus.UNAUTHORIZED; // 401
            } else if (ex.getMessage().contains("Forbidden") ||
                       ex.getMessage().contains("Access denied")) {
                status = HttpStatus.FORBIDDEN; // 403
            }
        }
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", "Une erreur interne s'est produite");
        errorResponse.put("details", ex.getMessage());
        
        // Log l'erreur complète pour le debugging
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}











