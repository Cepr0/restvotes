package restvotes.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Vote;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.service.PollService;
import restvotes.util.MessageService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link PollService}
 *
 * @author Cepro, 2017-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PollServiceImpl implements PollService {
    
    private final @NonNull PollRepo pollRepo;
    
    private final @NonNull VoteRepo voteRepo;
    
    private final @NonNull MessageService msgService;
    
    @Override
    public boolean closeAllUntil(LocalDate until) {
        try {
            int count = pollRepo.closeUntil(until);
            LOG.debug(msgService.logMessage("poll.is_disabled", until, count));
            return true;
        } catch (Exception e) {
            LOG.error(msgService.logMessage("poll.is_not_disabled", until, e.getMessage()));
            return false;
        }
    }
    
    // LazyInitializationException
    // http://stackoverflow.com/q/27115639/5380322
    // http://stackoverflow.com/q/26611173/5380322
    // http://stackoverflow.com/a/10466591/5380322
    @Override
    public Poll copyPrevious() {
        try {

            LocalDate today = LocalDate.now();
            Optional<Poll> lastPoll = pollRepo.getLast(today);

            if (lastPoll.isPresent()) {
                
                Poll poll = lastPoll.get();

                if (poll.getDate().isEqual(today)) {

                    LOG.error(msgService.logMessage("poll.is_not_copied", "Last Poll", "It has current date"));
                    return null;
                } else {

                    List<Menu> menus = poll.getMenus();
                    LOG.debug(msgService.logMessage("Last Poll menu size: %d", menus.size()));

                    Poll copy = pollRepo.saveAndFlush(new Poll(menus));
                    LOG.debug(msgService.logMessage("poll.is_copied", copy));
                    return copy;
                }
            } else {
                LOG.error(msgService.logMessage("poll.is_not_copied", "Last Poll", "Not found"));
                return null;
            }
        } catch (Exception e) {
            LOG.error(msgService.logMessage("poll.is_not_copied", "Last Poll", e.getMessage()));
            return null;
        }
    }
    
    @Override
    public void placeWinners() {
        
        // 1. Get all finished Polls without winners
        List<Poll> polls = pollRepo.getFinishedWithoutWinner();
        
        // 2. Make a loop through them
        for (Poll poll : polls) {
            
            // 3. For each one get <menuId, rank> pares, get the best one (they sorted by rank desc)
            // and place a winner to each Poll
            List<Vote.Rank> ranks = voteRepo.getRanksByDate(poll.getDate());
            if (!ranks.isEmpty()) {
                Menu menu = ranks.get(0).getMenu();
                Long menuId = menu.getId(); // ranks.get(0) - the best one
                try {
                    poll.setWinner(menu);
                    Poll savedPoll = pollRepo.save(poll);
                    LOG.debug(msgService.logMessage( "Placed a winner [id: %d] to Poll %s", menuId, savedPoll));
                } catch (Exception e) {
                    LOG.error(msgService.logMessage("A winner [id: %d] is not placed to Poll %s. Cause: ", menuId, poll, e.getMessage()));
                }
            }
        }
    }
    
    @Override
    public int deleteEmpty() {
        
        try {
            pollRepo.unlinkMenusFromFinishedAndWithoutVotes();
            int count = pollRepo.deleteFinishedAndWithoutVotes();
            if (count > 0) {
                LOG.debug(msgService.logMessage( "%d 'empty' Polls is deleted.", count));
            }
            return count;
        } catch (Exception e) {
            LOG.error(msgService.logMessage("Failed to delete 'empty' Polls. Cause: %s", e.getMessage()));
            return 0;
        }
    }
}
