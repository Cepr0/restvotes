package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Vote;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author Cepro, 2017-01-07
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/polls")
public class PollController {

    private final @NonNull RepositoryEntityLinks entityLinks;

    private final @NonNull PagedResourcesAssembler<Poll.Brief> pagedResourcesAssembler;

    private final @NonNull PollRepo pollRepo;
    
    private final @NonNull VoteRepo voteRepo;
    
    @GetMapping("/{date}/choice")
    public ResponseEntity<?> hasVoted(@PathVariable("date") LocalDate date) {
        
        return voteRepo.getByUserAndDate(AuthorizedUser.get(), date)
                       .map(this::getOkResponse)
                       .orElse(new ResponseEntity<>(NOT_FOUND));
    }
    
    private ResponseEntity<Resource<Vote.Brief>> getOkResponse(Vote.Brief vote) {
        
        Resource<Vote.Brief> resource = new Resource<>(vote);
        resource.add(entityLinks.linkForSingleResource(vote.getRestaurant()).withRel("restaurant"));
        resource.add(entityLinks.linkForSingleResource(vote.getMenu()).withRel("menu"));
        
        return ResponseEntity.ok(resource);
    }

    @GetMapping
    ResponseEntity<PagedResources<Resource<Poll.Brief>>> getPolls(Pageable pageable) {

        Page<Poll.Brief> pollPages = pollRepo.getAll(pageable);

        PagedResources<Resource<Poll.Brief>> pagedResources = pagedResourcesAssembler.toResource(pollPages);

        for (Resource<Poll.Brief> resource : pagedResources) {
            Poll.Brief poll = resource.getContent();
            resource.add(entityLinks.linkForSingleResource(Poll.class, poll.getDate()).withSelfRel());
        }
        return ResponseEntity.ok(pagedResources);
    }
    
    // http://stackoverflow.com/a/29924387/5380322
    // http://stackoverflow.com/a/31782016/5380322
    // http://stackoverflow.com/a/21362291/5380322
    @SuppressWarnings("unchecked")
    @GetMapping("/current")
    public ResponseEntity<Resource<?>> getCurrent(PersistentEntityResourceAssembler assembler) {
        Optional<Poll> pollOptional = pollRepo.getCurrent();
        
        if (pollOptional.isPresent()) {
            Poll poll = pollOptional.get();
            PersistentEntityResource resource = assembler.toFullResource(poll);
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
