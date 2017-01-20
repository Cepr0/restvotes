package restvotes.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Cepro, 2017-01-20
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class PollClosedException extends RuntimeException {
    public PollClosedException(String message) {
        super(message);
    }
}
