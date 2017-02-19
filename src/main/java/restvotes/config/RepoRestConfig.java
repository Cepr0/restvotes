package restvotes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.LocalTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * RepositoryRest configuration
 * @author Cepro, 2016-12-24
 */
@Configuration
public class RepoRestConfig extends RepositoryRestConfigurerAdapter {
    
    // To use non-HAL format:
    // http://stackoverflow.com/a/23287265/5380322
    // config.setDefaultMediaType(…)
    
    /**
     * Setting up which entities must expose their id
     * @param config
     */
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Poll.class);
        super.configureRepositoryRestConfiguration(config);
    }
    
    /**
     * Adding converters for {@link LocalDate} and {@link LocalTime}
     * @param conversionService
     */
    @Override
    public void configureConversionService(ConfigurableConversionService conversionService) {
        conversionService.addConverter(String.class, LocalDate.class, LocalDate::parse);
        conversionService.addConverter(String.class, LocalTime.class, time -> parse(time, ofPattern("yyyy-MM-dd HH:mm:ss")));
        super.configureConversionService(conversionService);
    }
    
    @Override
    public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
        // objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        // objectMapper.registerModule(new SimpleModule().addSerializer(Vote.class, new JsonSerializer<Vote>() {
        //     @Override
        //     public void serialize(Vote vote, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        //         gen.writeStartObject(vote);
        //         gen.writeObjectField("registered", vote.getRegistered());
        //         gen.writeEndObject();
        //     }
        // }));

        // objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // Hibernate5Module hibernate5Module = new Hibernate5Module();
        // hibernate5Module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
        // objectMapper.registerModule(hibernate5Module);

        super.configureJacksonObjectMapper(objectMapper);
    }
    
    /**
     * PUT and PATCH in SDR doesn't validated without this!!!
     * <p>http://stackoverflow.com/a/36814513/5380322</p>
     * @return local validator {@link LocalValidatorFactoryBean}
     */
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
    
    /**
     * Setting up validating for 'beforeCreate' and 'beforeSave' Repository events
     * <p>PUT and PATCH in SDR doesn't validated without this!!!</p>
     * <p>http://stackoverflow.com/a/36814513/5380322</p>
     * @param v
     */
    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener v) {
        // v.addValidator("beforeCreate", new UserValidator());
        // v.addValidator("beforeSave", new UserValidator());
        v.addValidator("beforeCreate", validator());
        v.addValidator("beforeSave", validator());
        super.configureValidatingRepositoryEventListener(v);
    }
}
