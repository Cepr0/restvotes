package restvotes.web.eventHandler;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;
import restvotes.AppProperties;
import restvotes.domain.entity.Poll;
import restvotes.repository.VoteRepo;
import restvotes.util.exception.ForbiddenException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Cepro, 2017-01-20
 */
@Slf4j
@Component
@AllArgsConstructor
@RepositoryEventHandler(Poll.class)
public class PollEventHandler {
    
    private final @NonNull AppProperties properties;
    
    private final @NonNull VoteRepo voteRepo;
    
    @HandleBeforeSave
    public void handleBeforeSave(Poll poll) throws Exception {
        checkClosedPoll(poll);
    }
    
    @HandleBeforeDelete
    public void handleBeforeDelete(Poll poll) throws Exception {
        checkClosedPoll(poll);
    }
    
    @HandleBeforeCreate
    public void handleBeforeCreate(Poll poll) {
        if (poll.getDate().isBefore(LocalDate.now())) {
            throw new ForbiddenException("poll.creating_in_the_past_are_forbidden");
        }
    
        LocalTime endOfVotingTimeValue = properties.getEndOfVotingTimeValue();
        String timeStr = endOfVotingTimeValue.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        if (poll.getDate().isEqual(LocalDate.now()) && LocalTime.now().isAfter(endOfVotingTimeValue)) {
            throw new ForbiddenException("poll.creating_after_finished_time", timeStr);
        }
    }

    @HandleAfterCreate
    @HandleAfterSave
    @HandleAfterDelete
    // @Cachevict(value = "polls", allEntries = true)
    public void handleAfter(Poll poll) {
        LocalDate id = poll.getId();
    }
    
    // TODO Add check for duplicate Restaurants in one Poll
    private void checkClosedPoll(Poll poll) {
        if (voteRepo.countByPoll(poll) != 0) {
            throw new ForbiddenException("poll.modifications_are_forbidden");
        }
    }
}
