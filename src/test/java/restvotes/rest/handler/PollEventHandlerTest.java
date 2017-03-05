package restvotes.rest.handler;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import restvotes.RestBaseTest;

import java.time.LocalDate;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static restvotes.DemoData.p2;
import static restvotes.DemoData.u1;

/**
 * @author Cepro, 2017-03-04
 */
public class PollEventHandlerTest extends RestBaseTest {
    
    private String pollUrl;
    private String menuUrl;
    
    @Override
    public void setUp() {
        super.setUp();
        
        pollUrl = BASE_PATH + POLL_PATH;
        menuUrl = BASE_PATH + MENU_PATH;
        
        userService.runAs(u1.getEmail());
    }
    
    @Test
    public void handleBeforeCreate() throws Exception {
        
        String menu1Url = menuUrl + "/1";
        String menu4Url = menuUrl + "/4";
        
        // The same date - forbidden
        String content = format("{\"date\": \"%s\", \"menus\": [\"%s\"]}",
                p2.getDate().format(ISO_LOCAL_DATE), getHref(menu1Url));
        postBadReq(content);
        
        // At the past date - forbidden
        content = format("{\"date\": \"%s\", \"menus\": [\"%s\"]}",
                LocalDate.now().minusDays(4).format(ISO_LOCAL_DATE), getHref(menu1Url));
        postBadReq(content);
        
        // Duplicated restaurants
        content = format("{\"menus\": [\"%s\", \"%s\"]}", getHref(menu1Url), getHref(menu4Url));
        postBadReq(content);
    }
    
    @Test
    public void handleBeforeSave() throws Exception {
    
        String url = pollUrl + "/" + p2.getDate().format(ISO_LOCAL_DATE);
        String content = format("{\"menus\": [\"%s\"]}", getHref(menuUrl + "/1"));
        
        mvc.perform(put(url)
                .content(content)
                .contentType(APPLICATION_JSON)
                .accept(HAL_JSON))
           .andExpect(status().isForbidden());
    }
    
    @Test
    public void handleBeforeDelete() throws Exception {
        
        mvc.perform(delete(pollUrl + "/" + p2.getDate().format(ISO_LOCAL_DATE)))
           .andExpect(status().isForbidden());
    }
    
    private String getHref(String url) throws Exception {
        
        MockHttpServletResponse response;
        response = mvc.perform(get(url))
                      .andExpect(status().isOk())
                      .andReturn().getResponse();
        
        String json = response.getContentAsString();
        return getDiscovererFor(response).findLinkWithRel(SELF_REL, json).getHref();
    }
    
    private void postBadReq(String content) throws Exception {
        mvc.perform(post(pollUrl)
                .content(content)
                .contentType(APPLICATION_JSON)
                .accept(HAL_JSON))
           .andExpect(status().isForbidden());
    }
}