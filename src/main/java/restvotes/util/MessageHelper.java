package restvotes.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.User;

import java.util.Locale;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Resolves messages from message.properties with {@link MessageSource}
 *
 * https://www.mkyong.com/spring/spring-how-to-access-messagesource-in-bean-messagesourceaware/
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#context-functionality-messagesource
 * http://stackoverflow.com/a/30558018/5380322
 * @author Cepro, 2017-02-08
 */
@RequiredArgsConstructor
@Component
public class MessageHelper {
    
    private final @NonNull MessageSource messageSource;
    
    /**
     * Makes message for output to {@link User} so it uses locale of {@link AuthorizedUser}
     * @param codeOrMessage property code or simple message
     * @param args arguments of the message
     * @return resolved message from .properties file
     */
    public String userMessage(String codeOrMessage, Object... args) {
        return messageSource.getMessage(
                codeOrMessage,
                args,
                format(requireNonNull(codeOrMessage, "The message must not be null!"), args),
                AuthorizedUser.locale() // User depended locale
        );
    }
    
    /**
     * Makes message for output to log so it uses default locale
     * @param codeOrMessage property code or simple message
     * @param args arguments of the message
     * @return resolved message from .properties file
     */
    public String logMessage(String codeOrMessage, Object... args) {
        return messageSource.getMessage(
                codeOrMessage,
                args,
                format(requireNonNull(codeOrMessage, "The message must not be null!"), args),
                Locale.getDefault()
        );
    }
}
