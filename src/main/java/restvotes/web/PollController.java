package restvotes.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;

import java.util.Optional;

/**
 * @author Cepro, 2017-01-07
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/polls")
@ExposesResourceFor(Poll.class)
public class PollController {
    
    private final RepositoryEntityLinks entityLinks;
    
    private final PollRepo pollRepo;
    
    private final PagedResourcesAssembler<Poll.Brief> pagedResourcesAssembler;
    
    private final PollResourceAssembler assembler;
    
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

    @GetMapping("/current")
    public ResponseEntity<Resource<?>> getCurrent(PersistentEntityResourceAssembler assembler) {
        Optional<Poll> pollOptional = pollRepo.getCurrent();

        if (pollOptional.isPresent()) {
            Poll poll = pollOptional.get();
            PersistentEntityResource resource = assembler.toFullResource(poll);
            // Resource<Poll> resource = new Resource<>(poll);
            // resource.add(entityLinks.linkForSingleResource(poll).withSelfRel());
    
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build(); //new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
