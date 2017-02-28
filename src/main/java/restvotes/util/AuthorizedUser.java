package restvotes.util;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import restvotes.domain.entity.User;

import java.util.Locale;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static restvotes.domain.entity.User.Role.ROLE_ADMIN;
import static restvotes.domain.entity.User.Role.ROLE_USER;

/**
 * Defines authorized user which used in {@link UserService}
 * @author Cepro, 2017-01-08
 */
public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    
    private final User user;
    
    public AuthorizedUser(User user) {
        super(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                (user.getRole() == ROLE_ADMIN) ? asList(ROLE_ADMIN, ROLE_USER) : singletonList(ROLE_USER));
        
        this.user = user;
    }
    
    /**
     * Get {@link User} instance from authorized user data in {@link SecurityContextHolder}
     * @return {@link User}
     * <p>(Perhaps we should move this method to {@link UserService})</p>
     */
    public static User get() {

        Authentication auth = getContext().getAuthentication();
        if (auth != null) {
            
            Object principal = auth.getPrincipal();
            if (principal instanceof AuthorizedUser) {
    
                AuthorizedUser authorizedUser = (AuthorizedUser) principal;
                return authorizedUser.getUser();
            }
        }
        
        // If auth is null then return a dummy User with ID = -1
        // to avoid NPE if we need to invoke get().getId(), for ex. in UserProfileController
        return (User) new User().setId(-1L);
     }
    
    /**
     * @return {@link Locale} of authorized user from {@link LocaleContextHolder}
     * <p>(Perhaps we should move this method to {@link UserService})</p>
     */
    // http://stackoverflow.com/a/16106304/5380322
    public static Locale locale() {
        return LocaleContextHolder.getLocale();
    }
    
    private User getUser() {
        return user;
    }
}
