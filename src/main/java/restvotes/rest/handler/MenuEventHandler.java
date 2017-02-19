package restvotes.rest.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.repository.VoteRepo;
import restvotes.util.MessageHelper;
import restvotes.util.exception.ForbiddenException;

/**
 * @author Cepro, 2017-02-06
 */
@RequiredArgsConstructor
@Component
@RepositoryEventHandler(Menu.class)
public class MenuEventHandler {
    
    private final @NonNull MessageHelper msgHelper;
    
    private final @NonNull VoteRepo voteRepo;
    
    @HandleBeforeSave
    public void handleBeforeSave(Menu menu) {
        
        // Updating Menu, that was already in use, is not allowed!
        if (voteRepo.findFirstByMenu(menu).isPresent()) {
            throw new ForbiddenException(msgHelper.userMessage("menu.already_in_use"));
        }
    }
}
