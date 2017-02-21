package restvotes.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import restvotes.BaseTest;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;

import java.time.LocalDate;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link PollService}
 * @author Cepro, 2017-02-20
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class PollServiceTest extends BaseTest {
    
    @Autowired
    private PollService pollService;
    
    @Autowired
    private PollRepo pollRepo;
    
    @Test
    public void closeAllUntil() throws Exception {
        Poll poll = pollRepo.save(new Poll());
        Integer count = pollService.closeAllUntil(LocalDate.now());
        assertThat(count, is (2));
    }
    
    @Test
    public void copyPrevious() throws Exception {
        
        Poll copy = pollService.copyPrevious();
        
        LocalDate now = LocalDate.now();
        assertThat(copy.getDate(), is(now));
        assertThat(copy.getMenus().stream().map(Menu::getId).collect(Collectors.toList()), hasItems(4L, 5L, 6L));
        assertThat(copy.getFinished(), is(false));
        assertThat(copy.getWinner(), is(nullValue()));
    }
    
    @Test
    public void placeWinners() throws Exception {
        
        pollService.placeWinners();
        
        LocalDate now = LocalDate.now();
        assertThat(pollRepo.getFinishedWithoutWinner(), hasSize(0));
        assertThat(pollRepo.getOne(now.minusDays(1)).getWinner(), is (nullValue()));
        assertThat(pollRepo.getOne(now.minusDays(2)).getWinner().getId(), is (2L));
    }
    
    @Test
    public void deleteEmpty() throws Exception {
        pollService.closeAllUntil(LocalDate.now());
        pollService.placeWinners();
        pollRepo.saveAndFlush(new Poll().setFinished(true));
    
        int count = pollService.deleteEmpty();
        assertThat(count, is(1));
    }
}