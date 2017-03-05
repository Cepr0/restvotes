package restvotes.rest.processor;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import restvotes.RestBaseTest;
import restvotes.service.PollService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static restvotes.DemoData.u1;
import static restvotes.DemoData.u3;

/**
 * @author Cepro, 2017-03-05
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class PollResourceProcessorsTest extends RestBaseTest {
    
    @Autowired
    private PollService pollService;
    
    private String pollUrl;
    
    @Before
    public void setUp() {
        super.setUp();
    
        pollService.closeAllUntil(LocalDate.now());
        pollService.placeWinners();
    
        pollUrl = BASE_PATH + POLL_PATH;
    }
    
    @Test
    public void PollBriefPagedAndSingleResourcesProcessor() throws Exception {
    
        userService.runAs(u1.getEmail());
        
        String json;
        json = mvc.perform(get(pollUrl))
                  .andExpect(status().isOk())
                  .andExpect(linkWithRelIsPresent(SELF_REL))
                  .andExpect(linkWithRelIsPresent(CURRENT_POLL_REL))
                  .andExpect(linkWithRelIsPresent(PROFILE_REL))
                  .andExpect(linkWithRelIsPresent(SEARCH_REL))
                  .andReturn().getResponse().getContentAsString();
    
        List<Link> selfLinks = JsonPath.read(json, "$._embedded.polls.*._links.self.href");
        assertThat(selfLinks, hasSize(2));
    
        List<Link> winnerLinks = JsonPath.read(json, "$._embedded.polls.*._links.winner.href");
        assertThat(winnerLinks, hasSize(2));
    }
    
    @Test
    public void PollResourceProcessor() throws Exception {
    
        userService.runAs(u3.getEmail());
        
        String json;
        json = mvc.perform(get(pollUrl + "/current"))
                  .andExpect(status().isOk())
                  .andExpect(linkWithRelIsPresent(SELF_REL))
                  .andExpect(linkWithRelIsPresent(WINNER_REL))
                  .andExpect(linkWithRelIsPresent(USER_CHOICE_REL))
                  .andExpect(jsonPath("$.current", is(true)))
                  .andExpect(jsonPath("$._embedded.menus[1].chosen", is(true)))
                  .andReturn().getResponse().getContentAsString();
        
        List<Integer> ranks = JsonPath.read(json, "$._embedded.menus.*.rank");
        assertThat(ranks, hasSize(3));
        assertThat(ranks, hasItems(0, 4, 2));
    }
}