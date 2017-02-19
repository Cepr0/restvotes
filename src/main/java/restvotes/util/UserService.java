package restvotes.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import restvotes.domain.entity.User;
import restvotes.repository.UserRepo;
import restvotes.util.exception.NotFoundException;

import java.util.Locale;
import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

/**
 * Implements a {@link UserDetailsService} interface
 *
 * @author Cepro, 2017-01-22
 */
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    
    private final @NonNull MessageHelper msgHelper;
    
    private final @NonNull UserRepo userRepo;
    
    @Override
    public AuthorizedUser loadUserByUsername(String email) throws UsernameNotFoundException {
        
        Optional<User> user = userRepo.findEnabledByEmail(email);
        if(user.isPresent()) {
            return new AuthorizedUser(user.get());
        } else {
            throw new NotFoundException(msgHelper.userMessage("users.with_email_not_found", email));
        }
    }
    
    /**
     * Used to simulate user authorization in tests
     * @param email user email
     */
    @Profile("test")
    public void runAs(String email) {
        AuthorizedUser user = loadUserByUsername(email);
        getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()));
    }
    
    /**
     * Used to simulate user authorization with any locales in tests
     * @param email user email
     * @param locale user locale
     */
    @Profile("test")
    public void runAs(String email, Locale locale) {
        AuthorizedUser user = loadUserByUsername(email);
        LocaleContextHolder.setLocale(locale);
        getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()));
    }
}
