package restvotes.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import restvotes.BaseTest;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.MenuItem;

import static java.math.BigDecimal.ONE;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Cepro, 2017-02-06
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class VoteRepoTest extends BaseTest {
    
    @Autowired
    private VoteRepo voteRepo;
    
    @Autowired
    private MenuRepo menuRepo;
    
    @Autowired
    private RestRepo restRepo;
    
    @Test
    public void findByMenuIsExists() throws Exception {
    
        Menu menuExisted = menuRepo.findOne(1L);
        boolean result = voteRepo.findFirstByMenu(menuExisted).isPresent();
        assertThat(result, is(true));
    
        Menu menu = menuRepo.save(new Menu(restRepo.getOne(1L),
                asList(new MenuItem("test1", ONE), new MenuItem("test2", ONE))));
        result = voteRepo.findFirstByMenu(menu).isPresent();
        assertThat(result, is(false));
        
    }
}