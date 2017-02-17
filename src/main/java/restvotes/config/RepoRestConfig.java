package restvotes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.LocalTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author Cepro, 2016-12-24
 */
@Configuration
public class RepoRestConfig extends RepositoryRestConfigurerAdapter {
    
    // To user non-HAL format:
    // http://stackoverflow.com/a/23287265/5380322
    // config.setDefaultMediaType(…)
    
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Poll.class);
        super.configureRepositoryRestConfiguration(config);
    }
    
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
    
    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener v) {
        // v.addValidator("beforeCreate", new UserValidator());
        // v.addValidator("beforeSave", new UserValidator());
        super.configureValidatingRepositoryEventListener(v);
    }
}
