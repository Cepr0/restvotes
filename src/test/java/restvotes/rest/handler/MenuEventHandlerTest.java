package restvotes.rest.handler;

import org.junit.Test;
import restvotes.RestBaseTest;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static restvotes.DemoData.*;

/**
 * @author Cepro, 2017-03-04
 */
public class MenuEventHandlerTest extends RestBaseTest {
    
    private String url;
    
    @Override
    public void setUp() {
        super.setUp();
        url = BASE_PATH + MENU_PATH + "/1";
    }
    
    @Test
    public void handleBeforeSave() throws Exception {
        
        userService.runAs(u1.getEmail());
        
        m1.setRestaurant(r2);
        mvc.perform(put(url)
                .content(mapper.writeValueAsString(m1))
                .contentType(APPLICATION_JSON)
                .accept(HAL_JSON))
           .andExpect(status().isForbidden());
    }
}