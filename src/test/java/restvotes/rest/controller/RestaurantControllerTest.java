package restvotes.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import restvotes.RestBaseTest;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static restvotes.DemoData.u1;
import static restvotes.rest.controller.MenuController.MENU_PATH;

/**
 * @author Cepro, 2017-03-04
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class RestaurantControllerTest extends RestBaseTest {
    
    private static final String RESTAURANT_ID = "/1";
    
    @Autowired
    private ObjectMapper mapper;
    
    @Test
    public void getMenus() throws Exception {
        
        String url = BASE_PATH + RESTAURANT_PATH + RESTAURANT_ID + MENU_PATH;
        
        userService.runAs(u1.getEmail());
        
        MockHttpServletResponse response;
        response = mvc.perform(get(url))
                      .andExpect(status().isOk())
                      .andExpect(linkWithRelIsPresent(SELF_REL))
                      .andExpect(jsonPath("$..menus.*", hasSize(2)))
                      .andExpect(jsonPath("$._embedded.menus[0]._links.self.href", endsWith(MENU_PATH + "/4")))
                      .andExpect(jsonPath("$._embedded.menus[1]._links.self.href", endsWith(MENU_PATH + "/1")))
                      .andReturn().getResponse();
        
        String json = response.getContentAsString();
        Link selfLink = getDiscovererFor(response).findLinkWithRel(SELF_REL, json);
        assertThat(selfLink.getHref().endsWith(RESTAURANT_ID + MENU_PATH), is(true));
        
    }
    
    @Test
    public void addMenu() throws Exception {
        
        String url = BASE_PATH + RESTAURANT_PATH + RESTAURANT_ID + MENU_PATH;
        
        userService.runAs(u1.getEmail());
        
        ResultActions action;
        action = mvc.perform(post(url)
                .content("{\"items\":[{\"description\":\"Desc1\",\"cost\":1},{\"description\":\"Desc2\",\"cost\":2}]}")
                .contentType(APPLICATION_JSON)
                .accept(HAL_JSON));
        
        MockHttpServletResponse response;
        response = action.andExpect(status().isCreated())
                         .andExpect(jsonPath("$.items.*", hasSize(2)))
                         .andExpect(linkWithRelIsPresent(SELF_REL))
                         .andExpect(linkWithRelIsPresent(RESTAURANT_REL))
                         .andReturn().getResponse();
        
        String json = response.getContentAsString();
        Link link = getDiscovererFor(response).findLinkWithRel(RESTAURANT_REL, json);
        assertThat(link.getHref().endsWith(RESTAURANT_PATH + RESTAURANT_ID), is(true));
        
        mvc.perform(post(url)
                .content("{\"items\":[{\"description\":\"D1\",\"cost\":0},{\"description\":\"D2\",\"cost\":2}]}")
                .contentType(APPLICATION_JSON)
                .accept(HAL_JSON))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.errors.*", hasSize(2)))
           .andExpect(jsonPath("$.errors[0].property", is("description")))
           .andExpect(jsonPath("$.errors[0].invalidValue", is("D1")))
           .andExpect(jsonPath("$.errors[1].property", is("cost")))
           .andExpect(jsonPath("$.errors[1].invalidValue", is(0)));
    }
}