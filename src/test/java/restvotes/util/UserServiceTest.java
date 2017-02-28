package restvotes.util;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import restvotes.BaseTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Cepro, 2017-02-28
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class UserServiceTest extends BaseTest {
    
    @Autowired
    private UserService userService;
    
    private static final String EMAIL = "frodo@restvotes.com";
    
    @Test
    public void loadUserByUsername() throws Exception {
        AuthorizedUser authorizedUser = userService.loadUserByUsername(EMAIL);
        String username = authorizedUser.getUsername();
        assertThat(username, is(EMAIL));
        
        exception.expect(UsernameNotFoundException.class);
        userService.loadUserByUsername("");
    }
}