package restvotes.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import restvotes.BaseTest;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author Cepro, 2017-01-22
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class PollRepoTest extends BaseTest {
    
    @Autowired private PollRepo pollRepo;
    
    private LocalDate pollDate1 = LocalDate.now().minusDays(1);
    
    @Test
    public void getLast() throws Exception {
        Optional<Poll> last = pollRepo.getLast(LocalDate.now());
        assertThat(last.isPresent(), is(true));
        
        if (last.isPresent()) {
            Poll poll = last.get();
            assertThat(poll.getDate(), is(pollDate1));
            List<Menu> menus = poll.getMenus();
            assertThat(menus, hasSize(3));
            
            Poll testPoll = new Poll(menus);
            assertThat(testPoll, notNullValue());
        }
    }
    
    @Test
    public void findByDate() throws Exception {
        Optional<Poll> poll2Optional = pollRepo.findByDate(pollDate1);
        boolean present = poll2Optional.isPresent();
        assertThat(present, is(true));
        
        if(present) {
            Poll poll2 = poll2Optional.get();
            List<Menu> menus = poll2.getMenus();
            assertThat(menus, hasSize(3));
    
            Poll poll3 = new Poll(menus);
            assertThat(poll3, notNullValue());
    
            Poll savedPoll = pollRepo.saveAndFlush(poll3);
            assertThat(savedPoll.getDate(), is(LocalDate.now()));
        }
    }
    
    @Test
    public void getFinishedWithoutWinner() throws Exception {
        int count = pollRepo.closeUntil(LocalDate.now());
        assertThat(count, is (1));
    
        List<Poll> polls = pollRepo.getFinishedWithoutWinner();
        assertThat(polls, hasSize(2));
    }
}