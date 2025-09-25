package co.unibague.agropecuario.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponseDTO {
    private String error;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(String error, String message, String path) {
        this();
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getters y Setters
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}