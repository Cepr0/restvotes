package restvotes.rest.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import restvotes.BaseTest;
import restvotes.util.UserService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static restvotes.DemoData.u1;
import static restvotes.rest.controller.MenuController.MENU_PATH;
import static restvotes.rest.controller.MenuController.VOTE_PATH;


/**
 * @author Cepro, 2017-03-01
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class MenuControllerTest extends BaseTest {

    @Value("${spring.data.rest.basePath}")
    private String BASE_PATH;
    
    private static final String MENU_ID = "/4";
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private UserService userService;
    
    private MockMvc mvc;
    
    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }
    
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