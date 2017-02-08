package restvotes.rest.handler;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.repository.VoteRepo;
import restvotes.util.exception.ForbiddenException;

/**
 * @author Cepro, 2017-02-06
 */
@Slf4j
@Component
@AllArgsConstructor
@RepositoryEventHandler(Menu.class)
public class MenuEventHandler {
    
    private final @NonNull VoteRepo voteRepo;
    
    @HandleBeforeSave
    public void handleBeforeSave(Menu menu) {
        
        // Updating Menu that was already in use is not allowed
        if (voteRepo.findFirstByMenu(menu).isPresent()) {
            throw new ForbiddenException("menu.already_in_use");
        }
    }
}
