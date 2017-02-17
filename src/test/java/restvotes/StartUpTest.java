package restvotes;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;
import restvotes.service.PollService;
import restvotes.service.VoteService;
import restvotes.util.UserService;

import java.time.LocalDate;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author Cepro, 2017-01-29
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class StartUpTest extends BaseTest {
    
    @Autowired
    private PollService pollService;
    
    @Autowired
    private VoteService voteService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PollRepo pollRepo;
    
    /**
     * Imitation of the several start App process
     * @throws Exception if something goes wrong
     */
    @Test
    public void startAppIntegrationTest() throws Exception {
    
        LocalDate now = LocalDate.now();
        
        // Close all unfinished Polls until now - must one
        assertThat(pollService.closeAllUntil(now), is(true));
        assertThat(pollRepo.getFirstUnfinished().isPresent(), is(false));
    
        // Placer winners in the closed Polls - for 2 day before Poll the winner id is 2, for 1 day before - 5
        pollService.placeWinners();
        assertThat(pollRepo.getFinishedWithoutWinner(), hasSize(0));
        assertThat(pollRepo.getOne(now.minusDays(1)).getWinner().getId(), is (5L));
        assertThat(pollRepo.getOne(now.minusDays(2)).getWinner().getId(), is (2L));
    
        // Copy previous Poll until now
        copyPrevious(now);
        
        // Close all unfinished Polls until now - new Poll must be finished
        assertThat(pollService.closeAllUntil(now), is(true));
        assertThat(pollRepo.getOne(now).getFinished(), is(true));
        
        // Delete 'empty' Poll - must be one
        assertThat(pollService.deleteEmpty(), is(1));
        assertThat(pollRepo.findByDate(now).isPresent(), is(false));
    
        // Copy previous Poll again
        Poll poll = copyPrevious(now);
        Menu menu = poll.getMenus().get(0);

        // Submit one vote fore 0th menu
        userService.runAs("frodo@restvotes.com");
        voteService.submitVote(menu);
    
        // Close unfinished Poll - new one must be closed
        assertThat(pollService.closeAllUntil(now), is(true));
        assertThat(pollRepo.getOne(now).getFinished(), is(true));
    
        // Place winner for closed Polls - last one winner is its 0th menu
        pollService.placeWinners();
        assertThat(pollRepo.getOne(now).getWinner(), is(menu));
        
        // Try to delete 'empty' Polls - they didn't
        assertThat(pollService.deleteEmpty(), is(0));
        assertThat(pollRepo.findByDate(now).isPresent(), is(true));
    }
    
    /**
     * Copy previous Poll until given date
     * <p>New Poll must:</p>
     * <p> - has the same date</p>
     * <p> - has menu ids: 4, 5, 6</p>
     * <p> - be unfinished</p>
     * <p> - has null Winner</p>
     * @param date operation date
     * @return the copy of the previous Poll or null
     */
    private Poll copyPrevious(LocalDate date) {
        assertThat(pollService.copyPrevious().getDate(), is(date));
        Poll poll = pollRepo.getOne(date);
        assertThat(poll.getDate(), is(date));
        assertThat(poll.getMenus().stream().map(Menu::getId).collect(Collectors.toList()), hasItems(4L, 5L, 6L));
        assertThat(poll.getFinished(), is(false));
        assertThat(poll.getWinner(), is(nullValue()));
        return poll;
    }
}