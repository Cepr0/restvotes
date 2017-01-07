package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;

/**
 * @author Cepro, 2017-01-07
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/polls")
@ExposesResourceFor(Poll.class)
public class PollController {
    
    private final @NonNull EntityLinks entityLinks;
    
    private final @NonNull PollRepo pollRepo;
    
    private final @NonNull PagedResourcesAssembler<Poll.Brief> pagedResourcesAssembler;
    
    @GetMapping
    @SuppressWarnings("unchecked")
    HttpEntity<PagedResources<Resource<Poll.Brief>>> getPolls(Pageable pageable) {
        
        Page<Poll.Brief> pollPages = pollRepo.getAll(pageable);
    
        PagedResources<Resource<Poll.Brief>> pagedResources = pagedResourcesAssembler.toResource(pollPages);
    
        for (Resource<Poll.Brief> resource : pagedResources) {
            Poll.Brief poll = resource.getContent();
            resource.add(entityLinks.linkForSingleResource(Poll.class, poll.getDate()).withSelfRel());
        }
        
        LinkBuilder linkBuilder = entityLinks.linkFor(Poll.class);
        pagedResources.add(linkBuilder.slash("current").withRel("current"));
        return new ResponseEntity<>(pagedResources, HttpStatus.OK);
    }
}
