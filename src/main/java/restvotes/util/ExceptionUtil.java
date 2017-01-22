package restvotes.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import restvotes.util.exception.ForbiddenException;
import restvotes.util.exception.NotFoundException;

import static restvotes.AuthorizedUser.locale;

/**
 * @author Cepro, 2017-01-21
 */

@Component
public class ExceptionUtil {
    
    private static MessageSource messages;
    
    @Autowired
    private ExceptionUtil(MessageSource source) {
        messages = source;
    }
    
    public static void exception(Type type, String codeOrMessage, Object... args) {
    
        String message = messages.getMessage(codeOrMessage, args, String.format(codeOrMessage, args), locale());
    
        switch (type) {
            case FORBIDDEN: throw new ForbiddenException(message);
            case NOT_FOUND: throw new NotFoundException(message);
            case USERNAME_NOT_FOUND: throw new UsernameNotFoundException(message);
        }
    }
    
    public static enum Type {
        FORBIDDEN, NOT_FOUND, USERNAME_NOT_FOUND;
    }
}
