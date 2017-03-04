package restvotes;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.LinkDiscoverers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import restvotes.util.UserService;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Cepro, 2017-03-03
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RestBaseTest {
    
    protected static final String POLL_PATH = "/polls";
    protected static final String SELF_REL = "self";
    protected static final String CURRENT_POLL_REL = "currentPoll";
    protected static final String PROFILE_REL = "profile";
    protected static final String SEARCH_REL = "search";
    protected static final String USER_CHOICE_REL = "userChoice";
    protected static final String WINNER_REL = "winner";
    
    @Value("${spring.data.rest.basePath}")
    protected String BASE_PATH;
    
    protected MockMvc mvc;
    
    @Autowired
    protected UserService userService;
    
    @Autowired
    private LinkDiscoverers links;
    
    @Autowired
    private WebApplicationContext context;
    
    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    /**
     * Creates a {@link ResultMatcher} that checks for the presence of a link with the given rel.
     *
     * @param rel
     * @return
     */
    protected ResultMatcher linkWithRelIsPresent(final String rel) {
        return new LinkWithRelMatcher(rel, true);
    }
    
    /**
     * Creates a {@link ResultMatcher} that checks for the non-presence of a link with the given rel.
     *
     * @param rel
     * @return
     */
    protected ResultMatcher linkWithRelIsNotPresent(String rel) {
        return new LinkWithRelMatcher(rel, false);
    }
    
    protected LinkDiscoverer getDiscovererFor(MockHttpServletResponse response) {
        return links.getLinkDiscovererFor(response.getContentType());
    }
    
    private class LinkWithRelMatcher implements ResultMatcher {
        
        private final String rel;
        private final boolean present;
        
        public LinkWithRelMatcher(String rel, boolean present) {
            this.rel = rel;
            this.present = present;
        }
        
        /*
         * (non-Javadoc)
         * @see org.springframework.test.web.servlet.ResultMatcher#match(org.springframework.test.web.servlet.MvcResult)
         */
        @Override
        public void match(MvcResult result) throws Exception {
            
            MockHttpServletResponse response = result.getResponse();
            String content = response.getContentAsString();
            LinkDiscoverer discoverer = links.getLinkDiscovererFor(response.getContentType());
            
            assertThat(discoverer.findLinkWithRel(rel, content), is(present ? notNullValue() : nullValue()));
        }
    }
}
