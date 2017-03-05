package restvotes.rest.processor;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import restvotes.RestBaseTest;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static restvotes.DemoData.u1;

/**
 * @author Cepro, 2017-03-05
 */
public class RestaurantResourceProcessorTest extends RestBaseTest {
    
    private static final String MENU_REL = "menus";
    
    @Test
    public void getRestaurant() throws Exception {
        
        String url = BASE_PATH + RESTAURANT_PATH + "/1";
        
        userService.runAs(u1.getEmail());
        
        String json;
        json = mvc.perform(get(url))
                  .andExpect(status().isOk())
                  .andExpect(linkWithRelIsPresent(SELF_REL))
                  .andExpect(linkWithRelIsPresent(RESTAURANT_REL))
                  .andExpect(linkWithRelIsPresent(MENU_REL))
                  .andReturn().getResponse().getContentAsString();

        String menusHref = JsonPath.read(json, "$._links.menus.href");
        assertThat(menusHref, endsWith("/1/menus"));
    }
}