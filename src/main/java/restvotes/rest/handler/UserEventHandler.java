package restvotes.rest.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.User;
import restvotes.util.MessageHelper;
import restvotes.util.exception.ForbiddenException;
import restvotes.util.exception.NotFoundException;

import java.util.Objects;

import static restvotes.domain.entity.User.Role.ROLE_ADMIN;
import static restvotes.util.AuthorizedUser.get;

/**
 * @author Cepro, 2017-01-23
 */
@RequiredArgsConstructor
@Component
@RepositoryEventHandler(User.class)
public class UserEventHandler {
    
    private final @NonNull MessageHelper msgHelper;
    
    @HandleBeforeCreate
    public void handleBeforeCreate(User user) {
        
        checkUser(user);
    }
    
    @HandleBeforeSave
    public void handleBeforeSave(User user) {
        
        checkUser(user);
    }
    
    private void checkUser(User user) {
        User u = get();
        if (u == null) {
            throw new NotFoundException(msgHelper.userMessage("users.not_found"));
        }
        
        if (u.getRole() != ROLE_ADMIN && !Objects.equals(u.getId(), user.getId())) {
            throw new ForbiddenException(msgHelper.userMessage("users.cannot_edit_another_user"));
        }

        if (u.getRole() != ROLE_ADMIN && user.getRole() == ROLE_ADMIN) {
            throw new ForbiddenException(msgHelper.userMessage("users.cannot_use_role_admin"));
        }
    }
}
