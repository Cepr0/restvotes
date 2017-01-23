package restvotes.web.eventHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.User;

import java.util.Objects;

import static restvotes.AuthorizedUser.get;
import static restvotes.domain.entity.User.Role.ROLE_ADMIN;
import static restvotes.util.ExceptionUtil.Type.FORBIDDEN;
import static restvotes.util.ExceptionUtil.exception;

/**
 * @author Cepro, 2017-01-23
 */
@Slf4j
@Component
@AllArgsConstructor
@RepositoryEventHandler(User.class)
public class UserEventHandler {
    
    @HandleBeforeCreate
    public void handleBeforeCreate(User user) {
        
        checkUser(user);
    }
    
    @HandleBeforeSave
    public void handleBeforeSave(User user) {
        
        checkUser(user);
    }
    
    private void checkUser(User user) {
        if (get().getRole() != ROLE_ADMIN && !Objects.equals(get().getId(), user.getId())) {
            exception(FORBIDDEN, "users.cannot_edit_another_user");
        }

        if (get().getRole() != ROLE_ADMIN && user.getRole() == ROLE_ADMIN) {
            exception(FORBIDDEN, "users.cannot_use_role_admin");
        }
    }
}
