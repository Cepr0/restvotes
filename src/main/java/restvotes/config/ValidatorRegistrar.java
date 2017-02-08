package restvotes.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;

/**
 * https://jira.spring.io/browse/DATAREST-524
 * http://www.baeldung.com/spring-data-rest-validators
 * http://stackoverflow.com/a/38668148/5380322
 *
 * @author Cepro, 2016-12-29
 */
@RequiredArgsConstructor
@Configuration
public class ValidatorRegistrar implements InitializingBean {
    
    private final ListableBeanFactory beanFactory;
    
    private final ValidatingRepositoryEventListener listener;
    
    private static final List<String> EVENTS = unmodifiableList(new ArrayList<>(Arrays.asList(
                "beforeCreate",
                "afterCreate",
                "beforeSave",
                "afterSave",
                "beforeLinkSave",
                "afterLinkSave",
                "beforeDelete",
                "afterDelete"
        )));
    
    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO http://stackoverflow.com/a/38668148/5380322
        Map<String, Validator> validators = beanFactory.getBeansOfType(Validator.class);
        for (Map.Entry<String, Validator> entry : validators.entrySet()) {
            EVENTS.stream()
                  .filter(p -> entry.getKey().startsWith(p))
                  .findFirst()
                  .ifPresent(p -> listener.addValidator(p, entry.getValue()));
        }
    }
}