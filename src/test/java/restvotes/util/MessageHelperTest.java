package restvotes.util;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import restvotes.BaseTest;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Cepro, 2017-02-09
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class MessageHelperTest extends BaseTest {
    
    @Autowired
    private MessageHelper msgHelper;
    
    @Autowired
    private UserService userService;
    
    @Test
    public void userMessage() throws Exception {
        
        userService.runAs("frodo@restvotes.com", new Locale("ru"));
        String message = msgHelper.userMessage("test.message_service_test");
        assertThat(message, is("MessageService тест"));

        userService.runAs("frodo@restvotes.com", new Locale("en"));
        message = msgHelper.userMessage("test.message_service_test");
        assertThat(message, is("MessageService test"));
    
        userService.runAs("frodo@restvotes.com", new Locale("de"));
        message = msgHelper.userMessage("test.message_service_test");
        assertThat(message, is("MessageService test"));
    }
    
    @Test
    public void logMessage() throws Exception {
    
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("ru"));
        String message = msgHelper.logMessage("test.message_service_test");
        assertThat(message, is("MessageService тест"));
        Locale.setDefault(defaultLocale);
    }
}