package restvotes.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import restvotes.BaseTest;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Vote;
import restvotes.repository.PollRepo;
import restvotes.util.AuthorizedUser;
import restvotes.util.UserService;
import restvotes.util.exception.NotFoundException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Cepro, 2017-02-21
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class VoteServiceTest extends BaseTest {
    
    @Autowired
    private VoteService voteService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PollRepo pollRepo;
    
    @Test
    public void submitVote() throws Exception {
        
        Optional<Poll> current = pollRepo.getCurrent();
        if (current.isPresent()) {
            
            Poll poll = current.get();
            Menu menu = poll.getMenus().get(0);
            userService.runAs("frodo@restvotes.com");
            Vote vote = voteService.submitVote(menu);
            assertThat(vote.getMenu(), is(menu));
            assertThat(vote.getUser(), is(AuthorizedUser.get()));
            assertThat(vote.getPoll(), is(poll));
        } else {
            throw new NotFoundException("Poll not found");
        }
    }
}