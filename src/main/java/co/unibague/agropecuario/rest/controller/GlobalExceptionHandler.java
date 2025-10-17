package co.unibague.agropecuario.rest.controller;

import co.unibague.agropecuario.rest.dto.ErrorResponseDTO;
import co.unibague.agropecuario.rest.exception.CosechaNotFoundException;
import co.unibague.agropecuario.rest.exception.ProductoAlreadyExistsException;
import co.unibague.agropecuario.rest.exception.ProductoNotFoundException;
import co.unibague.agropecuario.rest.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Manejador global de excepciones
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductoNotFound(
            ProductoNotFoundException ex, WebRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "PRODUCTO_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CosechaNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCosechaNotFound(
            CosechaNotFoundException ex, WebRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "COSECHA_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ProductoAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductoAlreadyExists(
            ProductoAlreadyExistsException ex, WebRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "PRODUCTO_ALREADY_EXISTS",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            ValidationException ex, WebRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "VALIDATION_ERROR",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }

        ErrorResponseDTO error = new ErrorResponseDTO(
                "VALIDATION_ERROR",
                "Errores de validación en los datos enviados",
                request.getDescription(false).replace("uri=", "")
        );
        error.setDetails(details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex, WebRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "INTERNAL_SERVER_ERROR",
                "Ha ocurrido un error interno del servidor",
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}