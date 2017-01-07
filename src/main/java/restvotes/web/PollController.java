package restvotes.web;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.AuditableBeanWrapperFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.rest.webmvc.*;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
@RepositoryRestController
@RequestMapping("/polls")
@ExposesResourceFor(Poll.class)
public class PollController {
    
    private final RepositoryEntityLinks entityLinks;
    
    private final PollRepo pollRepo;
    
    private final PagedResourcesAssembler<Poll.Brief> pagedResourcesAssembler;
    
    private final PollResourceAssembler assembler;
    
    private final HttpHeadersPreparer headersPreparer;
    
    @Autowired
    public PollController(RepositoryEntityLinks entityLinks,
                          PollRepo pollRepo,
                          PagedResourcesAssembler<Poll.Brief> pagedResourcesAssembler,
                          PollResourceAssembler assembler,
                          AuditableBeanWrapperFactory auditableBeanWrapperFactory) {
        this.entityLinks = entityLinks;
        this.pollRepo = pollRepo;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.assembler = assembler;
        this.headersPreparer = new HttpHeadersPreparer(auditableBeanWrapperFactory);
    }
    
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
    public ResponseEntity<Resource<?>> getCurrentPoll(PersistentEntityResourceAssembler assembler) {
    // }
    //
    // HttpEntity<Resource<Poll>> getCurrent() {
        Optional<Poll> pollOptional = pollRepo.getCurrent();

        if (pollOptional.isPresent()) {
            Poll poll = pollOptional.get();
            PersistentEntityResource resource = assembler.toFullResource(poll);
            // Resource<Poll> resource = new Resource<>(poll);
            // resource.add(entityLinks.linkForSingleResource(poll).withSelfRel());
            HttpHeaders responseHeaders = headersPreparer.prepareHeaders(resource);
    
            return new ResponseEntity<>(resource, responseHeaders, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build(); //new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
