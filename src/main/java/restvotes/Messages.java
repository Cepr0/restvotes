package restvotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static restvotes.AuthorizedUser.locale;

/**
 * @author Cepro, 2017-01-30
 */
@Component
public class Messages {

    private static MessageSource source;
    
    @Autowired
    private Messages(MessageSource messageSource) {
        source = messageSource;
    }
    
    public static MessageSource getSource() {
        return source;
    }
    
    public static String getMessage(String codeOrMessage, Object... args) {
        return source.getMessage(
                codeOrMessage,
                args,
                codeOrMessage != null ? String.format(codeOrMessage, args) : "Default message...",
                locale()
        );
    }
    
}
