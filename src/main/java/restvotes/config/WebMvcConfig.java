package restvotes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
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
    
    /**
     * Link local Validator ({@link LocalValidatorFactoryBean}) with our {@link ReloadableResourceBundleMessageSource} -
     * {@link WebMvcConfig#messageSource} to get reloaded constrain validation messages in our entities
     * <p>See e.g. {@link restvotes.domain.entity.User} </p>
     * <p>Pry here: http://stackoverflow.com/a/32736788/5380322</p>
     * @return
     */
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource());
        return validator;
    }
}
