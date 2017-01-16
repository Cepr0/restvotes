package restvotes.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Vote;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.service.PollService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static restvotes.util.LogUtils.debug;
import static restvotes.util.LogUtils.error;

/**
 * Implementation of {@link PollService}
 *
 * @author Cepro, 2017-01-15
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class PollServiceImpl implements PollService {
    
    private final PollRepo pollRepo;
    
    private final VoteRepo voteRepo;
    
    @Override
    // @CacheEvict(value = "polls")
    public boolean closeAllUntil(LocalDate until) {
        try {
            int count = pollRepo.disableUntil(until);
            debug(LOG, "poll.is_disabled", until, count);
            return true;
        } catch (Exception e) {
            error(LOG, "poll.is_not_disabled", until, e.getMessage());
            return false;
        }
    }
    
    @Override
    // @Cacheable("polls")
    @Transactional(readOnly = true)
    public Poll getOne(LocalDate date) {
        try {
            Poll poll = pollRepo.getOne(requireNonNull(date));
            debug(LOG, "poll.is_found", poll);
            return poll;
        } catch (Exception e) {
            error(LOG, "poll.is_not_found", date, e.getMessage());
            return null;
        }
    }


    // LazyInitializationException
    // http://stackoverflow.com/q/27115639/5380322
    // http://stackoverflow.com/q/26611173/5380322
    // http://stackoverflow.com/a/10466591/5380322
    @Override
//    @CacheEvict(value = "polls")
    public Poll copyPrevious() {
        try {
            LocalDate today = LocalDate.now();
            Optional<Poll> currentPoll = pollRepo.getLast(today);
            if (currentPoll.isPresent()) {
                
                Poll poll = currentPoll.get();
                if (poll.getDate().isEqual(today)) {
                    error(LOG, "poll.is_not_copied", "Last Poll", "It has current date");
                    return null;
                } else {
                    List<Menu> menus = poll.getMenus();
                    Poll p = new Poll(menus);
                    Poll copy = pollRepo.saveAndFlush(p);
                    debug(LOG, "poll.is_copied", copy);
                    return copy;
                }
            } else {
                error(LOG, "poll.is_not_copied", "Last Poll", "Not found");
                return null;
            }
        } catch (Exception e) {
            error(LOG, "poll.is_not_copied", "Last Poll", e.getMessage());
            return null;
        }
    }
    
    @Override
    // @CacheEvict(value = "polls")
    public Poll copyOf(Poll source) {
        try {
            List<Menu> menus = requireNonNull(source).getMenus();
            Poll poll = new Poll(menus);
            Poll copy = pollRepo.saveAndFlush(poll);
            debug(LOG, "poll.is_copied", copy);
            return copy;
        } catch (Exception e) {
            error(LOG, "poll.is_not_copied", source.getDate(), e.getMessage());
            return null;
        }
    }
    
    @Override
    // @CacheEvict(value = "polls")
    public void placeWinners() {
        
        // 1. Get all finished Polls without winners
        List<Poll> polls = pollRepo.getFinishedWithoutWinner();
        
        // 2. Make a loop through them
        for (Poll poll : polls) {
    
            // 3. For each one get <menuId, rank> pares, calculate the best and place a winner to current Poll
            List<Vote.Rank> ranks = voteRepo.getRanksByDate(poll.getDate());
            if (!ranks.isEmpty()) {
                Long menuId = ranks.get(0).getMenu().getId();
                try  {
                    if (pollRepo.placeWinner(poll.getDate(), menuId) > 0) {
                        debug(LOG, "Placed a winner [id: %d] to Poll %s", menuId, poll);
                    }
                } catch (Exception e) {
                    error(LOG, "A winner [id: %d] is not placed to Poll %s. Cause: ", menuId, poll, e.getMessage());
                }
            }
        }
    }
}
