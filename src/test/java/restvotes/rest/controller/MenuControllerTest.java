package restvotes.rest.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import restvotes.RestBaseTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static restvotes.DemoData.u1;
import static restvotes.rest.controller.MenuController.VOTE_PATH;


/**
 * @author Cepro, 2017-03-01
 */
public class MenuControllerTest extends RestBaseTest {

    private static final String MENU_ID = "/4";
    
    @Test
    public void submitVote() throws Exception {
    
        String url = BASE_PATH + MENU_PATH + MENU_ID + VOTE_PATH;
    
        userService.runAs(u1.getEmail());
    
        String href = JsonPath.parse(mvc
                .perform(put(url))
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent("restaurant"))
                .andExpect(linkWithRelIsPresent("menu"))
                .andExpect(linkWithRelIsPresent("poll"))
                .andReturn().getResponse().getContentAsString()).read("$._links.menu.href");
        
        assertThat(href.endsWith(MENU_PATH + MENU_ID), is(true));
    }
}