package restvotes;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import restvotes.domain.entity.User;

import java.util.Locale;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static restvotes.domain.entity.User.Role.ROLE_ADMIN;
import static restvotes.domain.entity.User.Role.ROLE_USER;

/**
 * @author Cepro, 2017-01-08
 */
public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    
    private static User user;
    
    public AuthorizedUser(User user) {
        super(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                (user.getRole() == ROLE_ADMIN) ? asList(ROLE_ADMIN, ROLE_USER) : singletonList(ROLE_USER));
        
        AuthorizedUser.user = user;
    }
    
    public static User get() {

        Authentication auth = getContext().getAuthentication();
        if (auth != null) {
            
            Object principal = auth.getPrincipal();
            if (principal instanceof AuthorizedUser) {
                
                String email = ((AuthorizedUser) principal).getUsername();
                if(email.equals(user.getEmail())) {
                    return user;
                }
            }
        }
        return null;
     }
    
    // http://stackoverflow.com/a/16106304/5380322
    public static Locale locale() {
        return LocaleContextHolder.getLocale();
    }
}
