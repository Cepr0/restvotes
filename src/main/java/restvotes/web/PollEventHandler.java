package restvotes.web;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Poll;
import restvotes.repository.VoteRepo;
import restvotes.util.PollClosedException;

/**
 * @author Cepro, 2017-01-20
 */
@Slf4j
@Component
@AllArgsConstructor
@RepositoryEventHandler(Poll.class)
public class PollEventHandler {
    
    private @NonNull VoteRepo voteRepo;
    
    @HandleBeforeSave
    public void handleBeforeSave(Poll poll) {
        checkPoll(poll);
    }
    
    @HandleBeforeDelete
    public void handleBeforeDelete(Poll poll) {
        checkPoll(poll);
    }
    
    //TODO Add BeforeCreate handle - check if date of new Poll is not less of current one
    
    private void checkPoll(Poll poll) {
        if (voteRepo.countByPoll(poll) != 0) {
            throw new PollClosedException("Changing or deleting a closed Poll is not allowed!");
        }
    }
}
