package restvotes.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.User;
import restvotes.repository.UserRepo;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static restvotes.util.ExceptionUtil.Type.USERNAME_NOT_FOUND;
import static restvotes.util.ExceptionUtil.exception;

/**
 * Implements a {@link UserDetailsService} interface
 *
 * @author Cepro, 2017-01-22
 */
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    
    private @NonNull UserRepo userRepo;
    
    @Override
    public AuthorizedUser loadUserByUsername(String email) throws UsernameNotFoundException {
        
        Optional<User> user = userRepo.findEnabledByEmail(email);
        if(user.isPresent()) {
            return new AuthorizedUser(user.get());
        } else {
            exception(USERNAME_NOT_FOUND, "User with email %s is not found", email);
            return null;
        }
    }
    
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
    
}
