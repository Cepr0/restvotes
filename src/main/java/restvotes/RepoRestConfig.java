package restvotes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;

/**
 * @author Cepro, 2016-12-24
 */
@Configuration
public class RepoRestConfig extends RepositoryRestConfigurerAdapter {
    
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Poll.class);
        super.configureRepositoryRestConfiguration(config);
    }
    
    @Override
    public void configureConversionService(ConfigurableConversionService conversionService) {
        conversionService.addConverter(String.class, LocalDate.class, LocalDate::parse);
        super.configureConversionService(conversionService);
    }
    
    @Override
    public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
        // objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        super.configureJacksonObjectMapper(objectMapper);
    }
    
    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        super.configureValidatingRepositoryEventListener(validatingListener);
    }
}
