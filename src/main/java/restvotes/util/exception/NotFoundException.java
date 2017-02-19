package restvotes.util.exception;

/**
 * Throw when we cannot find some resource
 * @author Cepro, 2017-01-30
 */
public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
}
