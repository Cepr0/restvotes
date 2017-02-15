package restvotes.rest.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import restvotes.domain.entity.User;

import java.util.regex.Pattern;

/**
 * http://docs.spring.io/spring-data/rest/docs/current/reference/html/#validation
 * http://www.baeldung.com/spring-data-rest-validators
 *
 * @author Cepro, 2017-02-13
 */
public class UserValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        
        User user = (User) target;
        String name = user.getName();
        String password = user.getPassword();
        String email = user.getEmail();
        Pattern pattern;
    
        pattern = Pattern.compile(User.NAME_PATTERN);
        if (name == null || !pattern.matcher(name).find()) {
            errors.rejectValue("name", "users.check_name");
        }
    
        pattern = Pattern.compile(User.PASSWORD_PATTERN);
        if (password == null || !pattern.matcher(password).find()) {
            errors.rejectValue("password", "users.check_password");
        }
        
        pattern = Pattern.compile(User.EMAIL_PATTERN);
        if (email == null || !pattern.matcher(email).find()) {
            errors.rejectValue("email", "users.check_email");
        }
    }
}
