package restvotes.util.exception;

/**
 * Throw when we need forbid some user action
 * @author Cepro, 2017-01-30
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String message) {
        super(message);
    }
}
