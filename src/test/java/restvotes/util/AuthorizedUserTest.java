package restvotes.util;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import restvotes.BaseTest;
import restvotes.domain.entity.User;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Cepro, 2017-02-28
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class AuthorizedUserTest extends BaseTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MessageHelper msgHelper;
    
    private static final String EMAIL = "frodo@restvotes.com";
    
    @Test
    public void get() throws Exception {
    
        userService.runAs(null);
        User user = AuthorizedUser.get();
        assertThat(user.getId(), is(-1L));
        
        userService.runAs(EMAIL);
        user = AuthorizedUser.get();
        assertThat(user.getEmail(), is(EMAIL));
    }
    
    @Test
    public void locale() throws Exception {
    
        userService.runAs(EMAIL, Locale.ENGLISH);
        Locale locale = AuthorizedUser.locale();
        assertThat(locale, is(Locale.ENGLISH));
    }
}