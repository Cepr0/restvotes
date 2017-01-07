package restvotes.web;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Poll;

/**
 * @author Cepro, 2017-01-07
 */
@Component
public class PollResourceAssembler extends ResourceAssemblerSupport<Poll, PollResource> {
    
    public PollResourceAssembler() {
        
        super(Poll.class, PollResource.class);
    }
    
    @Override
    public PollResource toResource(Poll poll) {
    
        return createResourceWithId(poll.getDate(), poll);
    }
}