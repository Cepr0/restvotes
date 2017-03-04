package restvotes.rest.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import restvotes.RestBaseTest;
import restvotes.service.PollService;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static restvotes.DemoData.*;

/**
 * @author Cepro, 2017-03-03
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class PollControllerTest extends RestBaseTest {
    
    @Autowired
    private PollService pollService;
    
    @Test
    public void getPolls() throws Exception {
        
        String url = BASE_PATH + POLL_PATH;
        
        userService.runAs(u1.getEmail());
        
        mvc.perform(get(url))
           .andExpect(status().isOk())
           .andExpect(linkWithRelIsPresent(SELF_REL))
           .andExpect(linkWithRelIsPresent(CURRENT_POLL_REL))
           .andExpect(linkWithRelIsPresent(PROFILE_REL))
           .andExpect(linkWithRelIsPresent(SEARCH_REL))
           .andExpect(jsonPath("$..polls.*", hasSize(2)))
           .andExpect(jsonPath("$._embedded.polls[0].date", is(MINUS_1_DAYS.format(ISO_LOCAL_DATE))))
           .andExpect(jsonPath("$._embedded.polls[1].date", is(MINUS_2_DAYS.format(ISO_LOCAL_DATE))))
           .andExpect(jsonPath("$._embedded.polls[0].finished", is(false)))
           .andExpect(jsonPath("$._embedded.polls[1].finished", is(true)))
           .andExpect(jsonPath("$._embedded.polls[0].current", is(true)))
           .andExpect(jsonPath("$._embedded.polls[1].current", is(false)))
           .andReturn().getResponse().getContentAsString();
    }
    
    @Test
    public void getCurrent() throws Exception {
        
        String url = BASE_PATH + POLL_PATH + "/current";
        
        pollService.closeAllUntil(LocalDate.now());
        pollService.placeWinners();
        
        userService.runAs(u3.getEmail());
        
        mvc.perform(get(url))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.date", is(MINUS_1_DAYS.format(ISO_LOCAL_DATE))))
           .andExpect(jsonPath("$.finished", is(true)))
           .andExpect(jsonPath("$.current", is(true)))
           .andExpect(jsonPath("$..menus.*", hasSize(3)))
           .andExpect(jsonPath("$._embedded.menus[0].chosen", is(false)))
           .andExpect(jsonPath("$._embedded.menus[0].rank", is(0)))
           .andExpect(jsonPath("$._embedded.menus[0].winner", is(false)))
           .andExpect(jsonPath("$._embedded.menus[1].chosen", is(true)))
           .andExpect(jsonPath("$._embedded.menus[1].rank", is(4)))
           .andExpect(jsonPath("$._embedded.menus[1].winner", is(true)))
           .andExpect(jsonPath("$._embedded.menus[2].chosen", is(false)))
           .andExpect(jsonPath("$._embedded.menus[2].rank", is(2)))
           .andExpect(jsonPath("$._embedded.menus[2].winner", is(false)))
           .andExpect(linkWithRelIsPresent(SELF_REL))
           .andExpect(linkWithRelIsPresent(USER_CHOICE_REL))
           .andExpect(linkWithRelIsPresent(WINNER_REL))
           .andReturn().getResponse().getContentAsString();
    }
}