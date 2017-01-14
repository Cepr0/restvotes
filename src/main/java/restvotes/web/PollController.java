package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
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
public class PollController {

    // private final @NonNull RepositoryEntityLinks entityLinks;
    //
    private final @NonNull PagedResourcesAssembler<Poll.Brief> assembler;

    private final @NonNull PollRepo pollRepo;
    
    // private final @NonNull VoteRepo voteRepo;
    //
    // @GetMapping("/{date}/choice")
    // public ResponseEntity<?> hasVoted(@PathVariable("date") LocalDate date) {
    //
    //     return voteRepo.getByUserAndDate(AuthorizedUser.get(), date)
    //                    .map(this::getOkResponse)
    //                    .orElse(new ResponseEntity<>(NOT_FOUND));
    // }
    //
    // private ResponseEntity<Resource<Vote.Brief>> getOkResponse(Vote.Brief vote) {
    //
    //     Resource<Vote.Brief> resource = new Resource<>(vote);
    //     resource.add(entityLinks.linkForSingleResource(vote.getRestaurant()).withRel("restaurant"));
    //     resource.add(entityLinks.linkForSingleResource(vote.getMenu()).withRel("menu"));
    //
    //     return ResponseEntity.ok(resource);
    // }

    @GetMapping
    ResponseEntity<PagedResources<Resource<Poll.Brief>>> getPolls(Pageable pageable) {

        return ResponseEntity.ok(assembler.toResource(pollRepo.getAll(pageable)));
    }
    
    // http://stackoverflow.com/a/29924387/5380322
    // http://stackoverflow.com/a/31782016/5380322
    // http://stackoverflow.com/a/21362291/5380322
    // @SuppressWarnings("unchecked")
    // @GetMapping("/current")
    // public ResponseEntity<Resource<?>> getCurrent(PersistentEntityResourceAssembler assembler) {
    //     Optional<Poll> pollOptional = pollRepo.getCurrent();
    //
    //     if (pollOptional.isPresent()) {
    //         Poll poll = pollOptional.get();
    //         PersistentEntityResource resource = assembler.toFullResource(poll);
    //         return ResponseEntity.ok(resource);
    //     } else {
    //         return ResponseEntity.notFound().build();
    //     }
    // }
}
