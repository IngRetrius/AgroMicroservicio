package co.unibague.agropecuario.rest.exception;

/**
 * Excepción lanzada cuando no se encuentra una cosecha
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 */
public class CosechaNotFoundException extends RuntimeException {

    public CosechaNotFoundException(String message) {
        super(message);
    }

    public CosechaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}