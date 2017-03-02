package restvotes;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.LinkDiscoverers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Cepro, 2016-12-01
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
abstract public class BaseTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Autowired
    private LinkDiscoverers links;
    
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
