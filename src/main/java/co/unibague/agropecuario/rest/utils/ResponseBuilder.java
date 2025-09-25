package co.unibague.agropecuario.rest.utils;

import co.unibague.agropecuario.rest.dto.ApiResponseDTO;
import co.unibague.agropecuario.rest.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para construir respuestas HTTP estandarizadas
 */
public class ResponseBuilder {

    private ResponseBuilder() {} // Clase utilitaria

    // ===== RESPUESTAS EXITOSAS =====

    public static <T> ResponseEntity<ApiResponseDTO<T>> success(T data) {
        return ResponseEntity.ok(ApiResponseDTO.success(data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> success(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.success(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Recurso creado exitosamente", data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(message, data));
    }

    public static ResponseEntity<ApiResponseDTO<Void>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponseDTO.success("Operación completada exitosamente", null));
    }

    public static ResponseEntity<ApiResponseDTO<Void>> noContent(String message) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponseDTO.success(message, null));
    }

    // ===== RESPUESTAS DE ERROR =====

    public static ResponseEntity<ErrorResponseDTO> badRequest(String message) {
        return badRequest(message, null);
    }

    public static ResponseEntity<ErrorResponseDTO> badRequest(String message, String path) {
        ErrorResponseDTO error = new ErrorResponseDTO("BAD_REQUEST", message, path);
        return ResponseEntity.badRequest().body(error);
    }

    public static ResponseEntity<ErrorResponseDTO> badRequest(String message, List<String> details, String path) {
        ErrorResponseDTO error = new ErrorResponseDTO("BAD_REQUEST", message, path);
        error.setDetails(details);
        return ResponseEntity.badRequest().body(error);
    }

    public static ResponseEntity<ErrorResponseDTO> notFound(String message) {
        return notFound(message, null);
    }

    public static ResponseEntity<ErrorResponseDTO> notFound(String message, String path) {
        ErrorResponseDTO error = new ErrorResponseDTO("NOT_FOUND", message, path);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    public static ResponseEntity<ErrorResponseDTO> conflict(String message) {
        return conflict(message, null);
    }

    public static ResponseEntity<ErrorResponseDTO> conflict(String message, String path) {
        ErrorResponseDTO error = new ErrorResponseDTO("CONFLICT", message, path);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    public static ResponseEntity<ErrorResponseDTO> internalError(String message) {
        return internalError(message, null);
    }

    public static ResponseEntity<ErrorResponseDTO> internalError(String message, String path) {
        ErrorResponseDTO error = new ErrorResponseDTO("INTERNAL_SERVER_ERROR",
                message != null ? message : "Ha ocurrido un error interno del servidor", path);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public static ResponseEntity<ErrorResponseDTO> unauthorized(String message) {
        ErrorResponseDTO error = new ErrorResponseDTO("UNAUTHORIZED", message, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    public static ResponseEntity<ErrorResponseDTO> forbidden(String message) {
        ErrorResponseDTO error = new ErrorResponseDTO("FORBIDDEN", message, null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // ===== RESPUESTAS ESPECIALES =====

    public static <T> ResponseEntity<ApiResponseDTO<T>> paginatedSuccess(
            String message, T data, int totalElements, int totalPages, int currentPage) {

        ApiResponseDTO<T> response = ApiResponseDTO.success(message, data);
        // Agregar metadatos de paginación si fuera necesario
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<ApiResponseDTO<Map<String, Object>>> statistics(Map<String, Object> stats) {
        return ResponseEntity.ok(
                ApiResponseDTO.success("Estadísticas obtenidas exitosamente", stats));
    }

    public static ResponseEntity<ApiResponseDTO<Object>> healthCheck() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "Agropecuario REST API"
        );
        return ResponseEntity.ok(ApiResponseDTO.success("Servicio funcionando correctamente", health));
    }

    // ===== MÉTODOS DE VALIDACIÓN =====

    public static <T> ResponseEntity<ErrorResponseDTO> validationError(List<String> errors, String path) {
        ErrorResponseDTO error = new ErrorResponseDTO("VALIDATION_ERROR",
                "Errores de validación en los datos enviados", path);
        error.setDetails(errors);
        return ResponseEntity.badRequest().body(error);
    }

    public static ResponseEntity<ErrorResponseDTO> customError(HttpStatus status, String errorCode,
                                                               String message, String path) {
        ErrorResponseDTO error = new ErrorResponseDTO(errorCode, message, path);
        return ResponseEntity.status(status).body(error);
    }

    // ===== BUILDERS FLUIDOS =====

    public static class SuccessBuilder<T> {
        private String message;
        private T data;
        private HttpStatus status = HttpStatus.OK;

        public SuccessBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public SuccessBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public SuccessBuilder<T> status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public ResponseEntity<ApiResponseDTO<T>> build() {
            ApiResponseDTO<T> response = message != null ?
                    ApiResponseDTO.success(message, data) :
                    ApiResponseDTO.success(data);
            return ResponseEntity.status(status).body(response);
        }
    }

    public static <T> SuccessBuilder<T> success() {
        return new SuccessBuilder<>();
    }
}