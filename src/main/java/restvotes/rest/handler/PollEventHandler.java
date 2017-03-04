package restvotes.rest.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;
import restvotes.config.AppConfig;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.util.MessageHelper;
import restvotes.util.exception.ForbiddenException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

/**
 * {@link Poll} related events handlers
 * @author Cepro, 2017-01-20
 */
@Slf4j
@RequiredArgsConstructor
@Component
@RepositoryEventHandler(Poll.class)
public class PollEventHandler {
    
    private final @NonNull AppConfig config;
    
    private final @NonNull VoteRepo voteRepo;
    
    private final @NonNull PollRepo pollRepo;
    
    private final @NonNull MessageHelper msgHelper;
    
    @HandleBeforeSave
    public void handleBeforeSave(Poll poll) {
    
        // If Poll has Votes
        checkIfPollhasVotes(poll);
    
        // If Menus has duplicate restaurants
        checkRestaurantDuplicates(poll);
    }
    
    @HandleBeforeDelete
    public void handleBeforeDelete(Poll poll) {
        // If Poll has Votes
        checkIfPollhasVotes(poll);
    }
    
    @HandleBeforeCreate
    public void handleBeforeCreate(Poll poll) {
    
        // Check if a Poll for this date is already exists
        if (pollRepo.findByDate(poll.getDate()).isPresent()) {
    
            String message = msgHelper.logMessage("poll.is_already_exists");
            LOG.error(message);
            throw new ForbiddenException(message);
        }
    
        // If we are trying to create Poll in the Past
        if (poll.getDate().isBefore(LocalDate.now())) {
            
            String message = msgHelper.logMessage("poll.creating_in_the_past_are_forbidden");
            LOG.error(message);
            throw new ForbiddenException(message);
        }
    
        // If Menus has duplicate restaurants
        checkRestaurantDuplicates(poll);

        // If we are trying to create Poll after the End Of Voting Time
        LocalTime endOfVotingTimeValue = config.getEndOfVotingTimeValue();
        String timeStr = endOfVotingTimeValue.format(DateTimeFormatter.ofPattern("HH:mm"));
        if (poll.getDate().isEqual(LocalDate.now()) && LocalTime.now().isAfter(endOfVotingTimeValue)) {
            
            String message = msgHelper.logMessage("poll.creating_after_finished_time", timeStr);
            LOG.error(message);
            throw new ForbiddenException(message);
        }
    }

    @HandleAfterCreate
    @HandleAfterSave
    @HandleAfterDelete
    // @Cachevict(value = "polls", allEntries = true)
    public void handleAfter(Poll poll) {
        LOG.debug(msgHelper.logMessage("Poll %s is changed", poll.toString()));
    }
    
    private void checkIfPollhasVotes(Poll poll) {
        if (voteRepo.countByPoll(poll) != 0) {
            
            String message = msgHelper.logMessage("poll.modifications_are_forbidden");
            LOG.error(message);
            throw new ForbiddenException(message);
        }
    }
    
    private void checkRestaurantDuplicates(Poll poll) {
        // http://stackoverflow.com/a/30053822/5380322
        List<Menu> menus = poll.getMenus();
        boolean hasNoDuplicates = menus.stream().mapToLong(menu -> menu.getRestaurant().getId()).allMatch(new HashSet<>()::add);
        if (!hasNoDuplicates) {
            
            String message = msgHelper.logMessage("poll.has_duplicates");
            LOG.error(message);
            throw new ForbiddenException(message);
        }
    }
}
