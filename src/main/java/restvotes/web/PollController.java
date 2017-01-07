package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;

/**
 * @author Cepro, 2017-01-07
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/polls")
@ExposesResourceFor(Poll.class)
// TODO Change to @RepositoryRestController: http://stackoverflow.com/questions/33571920/
public class PollController {
    private final @NonNull EntityLinks entityLinks;
    
    private final @NonNull PollRepo pollRepo;
    
    @GetMapping
    @SuppressWarnings("unchecked")
    HttpEntity<PagedResources<Poll.Brief>> getPolls(Pageable pageable, PagedResourcesAssembler assembler) {
        
        Page<Poll.Brief> pollPages = pollRepo.getAll(pageable);
        
        PagedResources<Poll.Brief> pagedResources = assembler.toResource(pollPages);
        
        for (Object r : pagedResources) {
            Resource resource = ((Resource) r);
            Poll.Brief poll = (Poll.Brief) resource.getContent();
            resource.add(entityLinks.linkForSingleResource(Poll.class, poll.getDate()).withSelfRel());
        }
        
        LinkBuilder linkBuilder = entityLinks.linkFor(Poll.class);
        pagedResources.add(linkBuilder.slash("current").withRel("current"));
        return new ResponseEntity<>(pagedResources, HttpStatus.OK);
    }
}
