package restvotes.rest.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static restvotes.domain.entity.User.*;

/**
 * @author Cepro, 2017-01-25
 */
@Getter
@NoArgsConstructor
@Relation(value = "profile")
@JsonInclude(NON_NULL)
public class UserProfile implements Identifiable<Long> {

    @JsonIgnore
    private Long id;
    
    @NotNull
    @Pattern(regexp = NAME_PATTERN, message = "users.check_name")
    private String name;
    
    @NotNull
    @Pattern(regexp = PASSWORD_PATTERN, message = "users.check_password")
    private String password;
    
    @NotNull
    @Pattern(regexp = EMAIL_PATTERN, message = "users.check_email")
    private String email;

    public UserProfile(User user) {
        id = user.getId();
        name = user.getName();
        password = "";
        email = user.getEmail();
    }

    @Override
    public Long getId() {
        return id;
    }
}
