package restvotes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * WebMvc configuration
 * @author Cepro, 2017-02-16
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    
    /**
     * Setting up {@link ReloadableResourceBundleMessageSource} to auto-reload changes of messages.properties
     * <p>Pry here: http://stackoverflow.com/a/30558018/5380322</p>
     * @return a ReloadableResourceBundleMessageSource instance
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("file:./config/messages");
        messageSource.setCacheSeconds(10);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
