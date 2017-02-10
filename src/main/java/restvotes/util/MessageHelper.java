package restvotes.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * https://www.mkyong.com/spring/spring-how-to-access-messagesource-in-bean-messagesourceaware/
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#context-functionality-messagesource
 * http://stackoverflow.com/a/30558018/5380322
 * @author Cepro, 2017-02-08
 */
@RequiredArgsConstructor
@Component
public class MessageHelper {
    
    private final @NonNull MessageSource messageSource;
    
    public String userMessage(String codeOrMessage, Object... args) {
        return messageSource.getMessage(
                codeOrMessage,
                args,
                format(requireNonNull(codeOrMessage, "The message must not be null!"), args),
                AuthorizedUser.locale() // User depended locale
        );
    }
    
    public String logMessage(String codeOrMessage, Object... args) {
        return messageSource.getMessage(
                codeOrMessage,
                args,
                format(requireNonNull(codeOrMessage, "The message must not be null!"), args),
                Locale.getDefault()
        );
    }
}
