package co.unibague.agropecuario.rest.exception;

public class ProductoAlreadyExistsException extends RuntimeException {
    public ProductoAlreadyExistsException(String message) {
        super(message);
    }

    public ProductoAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}