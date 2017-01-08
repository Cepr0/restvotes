package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.User;

import java.util.Collection;

/**
 * @author Cepro, 2016-12-21
 */
@Component
@RequiredArgsConstructor
public class ResourceProcessors {
    
    private final @NonNull RepositoryEntityLinks entityLinks;
    
    @Component
    public class PollResourceProcessor implements ResourceProcessor<Resource<Poll>> {
        
        @Override
        public Resource<Poll> process(Resource<Poll> resource) {
            Poll poll = resource.getContent();
            if (!poll.isFinished()) {
                poll.getMenus().forEach(menu -> resource.add(entityLinks.linkForSingleResource(menu).slash("vote").withRel("vote")));
                resource.add(entityLinks.linkFor(User.class).slash("choice").withRel("choice"));
            }
            return resource;
        }
    }
    
    @Component
    public class MenuDetailedResourcesProcessor implements ResourceProcessor<Resources<Resource<Menu.Detailed>>> {
        
        @Override
        public Resources<Resource<Menu.Detailed>> process(Resources<Resource<Menu.Detailed>> resource) {
            Collection<Resource<Menu.Detailed>> detaileds = resource.getContent();
            return resource;
        }
    }
    
    @Component
    public class MenuResourcesProcessor implements ResourceProcessor<Resources<Resource<Menu>>> {
        
        @Override
        public Resources<Resource<Menu>> process(Resources<Resource<Menu>> resource) {
            Collection<Resource<Menu>> detaileds = resource.getContent();
            return resource;
        }
    }
}
