package restvotes.web;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Poll;
import restvotes.repository.VoteRepo;

import java.time.LocalDate;

import static restvotes.util.ExceptionUtil.Type.FORBIDDEN;
import static restvotes.util.ExceptionUtil.exception;

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
    
    @HandleBeforeCreate
    public void handleBeforeCreate(Poll poll) {
        if (poll.getDate().isBefore(LocalDate.now())) {
            exception(FORBIDDEN, "Creating a Poll in the past is not allowed!");
        }
    }
    
    private void checkPoll(Poll poll) {
        if (voteRepo.countByPoll(poll) != 0) {
            exception(FORBIDDEN, "Changing or deleting a closed Poll is not allowed!");
        }
    }
}
