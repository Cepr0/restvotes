package restvotes.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import restvotes.Messages;
/**
 * @author Cepro, 2017-01-30
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String codeOrMessage, Object... args) {
        super(Messages.getMessage(codeOrMessage, args));
    }
}
