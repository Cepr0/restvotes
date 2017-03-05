package restvotes.rest.processor;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import restvotes.RestBaseTest;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Cepro, 2017-03-05
 */
public class RootResourceProcessorTest extends RestBaseTest {

    @Test
    public void getRoot() throws Exception {
    
        String url = BASE_PATH;
    
        String json;
        json = mvc.perform(get(url))
                  .andExpect(status().isOk())
                  .andExpect(linkWithRelIsPresent(CURRENT_POLL_REL))
                  .andExpect(linkWithRelIsPresent(USER_PROFILE_REL))
                  .andReturn().getResponse().getContentAsString();
    
        String curPollHref = JsonPath.read(json, "$._links.currentPoll.href");
        assertThat(curPollHref, endsWith("/polls/current"));
    
        String userProfileHref = JsonPath.read(json, "$._links.userProfile.href");
        assertThat(userProfileHref, endsWith("/userProfile"));
    }
}