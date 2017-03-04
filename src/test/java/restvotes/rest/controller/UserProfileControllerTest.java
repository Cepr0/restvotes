package restvotes.rest.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import restvotes.RestBaseTest;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static restvotes.DemoData.u1;

/**
 * @author Cepro, 2017-03-04
 */
public class UserProfileControllerTest extends RestBaseTest {
    
    private String url;
    
    @Override
    public void setUp() {
        super.setUp();
        url = BASE_PATH + USER_PROFILE_PATH;
    }
    
    @Test
    public void getProfile() throws Exception {
        
        userService.runAs(u1.getEmail());
        
        mvc.perform(get(url))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name", is(u1.getName())))
           .andExpect(jsonPath("$.password", is("")))
           .andExpect(jsonPath("$.email", is(u1.getEmail())))
           .andExpect(linkWithRelIsPresent(USER_PROFILE_REL));
    }
    
    @Test
    public void signUp() throws Exception {
    
        ResultActions result = mvc.perform(post(url)
                .content("{\"name\": \"User\", \"password\": \"123456\", \"email\": \"user@restvotes.com\"}")
                .contentType(APPLICATION_JSON)
                .accept(HAL_JSON)).andExpect(status().isCreated());
    
        checkUserOk(result);
    
        result = mvc.perform(post(url)
                .content("{\"name\": \"U\", \"password\": \"1\", \"email\": \"u\"}")
                .contentType(APPLICATION_JSON)
                .accept(HAL_JSON)).andExpect(status().isBadRequest());
    
        checkUserBad(result);
    }
    
    @Test
    public void updateProfile() throws Exception {
        
        userService.runAs(u1.getEmail());
        ResultActions result = mvc.perform(put(url)
                .content("{\"name\": \"User\", \"password\": \"123456\", \"email\": \"user@restvotes.com\"}")
                .contentType(APPLICATION_JSON)
                .accept(HAL_JSON)).andExpect(status().isOk());
        
        checkUserOk(result);
        
        result = mvc.perform(put(url)
                .content("{\"name\": \"U\", \"password\": \"1\", \"email\": \"u\"}")
                .contentType(APPLICATION_JSON)
                .accept(HAL_JSON)).andExpect(status().isBadRequest());
        
        checkUserBad(result);
    }

    private void checkUserOk(ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.name", is("User")))
              .andExpect(jsonPath("$.password", is("")))
              .andExpect(jsonPath("$.email", is("user@restvotes.com")))
              .andExpect(linkWithRelIsPresent(USER_PROFILE_REL));
    }

    private void checkUserBad(ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.errors.*", hasSize(3)))
              .andExpect(jsonPath("$.errors.*.property", hasItems("name", "password", "email")));
    }
}