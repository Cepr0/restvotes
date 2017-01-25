package restvotes.web.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.User;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

/**
 * @author Cepro, 2017-01-25
 */
@Getter
@Relation(value = "profile")
@JsonInclude(NON_NULL)
public class UserProfile implements Identifiable<Long> {

    @JsonIgnore
    Long id;

    String name;

    @JsonProperty(access = WRITE_ONLY)
    String password;

    String email;

    public UserProfile() {
    }

    public UserProfile(User user) {
        id = user.getId();
        name = user.getName();
        password = user.getPassword();
        email = user.getEmail();
    }

    @Override
    public Long getId() {
        return id;
    }
}
