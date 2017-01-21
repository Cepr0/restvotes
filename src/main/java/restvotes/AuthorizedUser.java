package restvotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.User;
import restvotes.repository.UserRepo;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Cepro, 2017-01-08
 */
@Component
public class AuthorizedUser {
    
    private static UserRepo repo;
    
    @Autowired
    public AuthorizedUser(UserRepo userRepo) {
        repo = userRepo;
    }
    
    public static User get() {
        return repo.getOne(ThreadLocalRandom.current().nextLong(1, 9));
    }
    
    public static Locale locale() {
        return null;
    }
}
