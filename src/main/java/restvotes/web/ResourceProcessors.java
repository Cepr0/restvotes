package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Poll;

/**
 * @author Cepro, 2016-12-21
 */
@Component
@RequiredArgsConstructor
public class ResourceProcessors {

    private final @NonNull EntityLinks entityLinks;
    
    @Component
    public class PollResourceProcessor implements ResourceProcessor<Resource<Poll>> {
        
        @Override
        public Resource<Poll> process(Resource<Poll> resource) {
            Poll poll = resource.getContent();
            if (!poll.isFinished()) {
                poll.getMenus().forEach(menu -> resource.add(entityLinks.linkForSingleResource(menu).slash("vote").withRel("vote")));
            }
            return resource;
        }
    }
}
