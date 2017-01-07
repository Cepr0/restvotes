package restvotes.web;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import restvotes.domain.entity.Poll;

/**
 * @author Cepro, 2017-01-07
 */
public class PollResource extends Resource<Poll> {
    public PollResource() {
        super(null);
    }
    
    public PollResource(Poll content, Link... links) {
        super(content, links);
    }
}