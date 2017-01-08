package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.Vote;
import restvotes.repository.VoteRepo;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author Cepro, 2017-01-06
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/users")
public class UserController {
    
    private final RepositoryEntityLinks entityLinks;
    
    private final @NonNull VoteRepo voteRepo;
    
    @GetMapping("/hasVoted")
    public ResponseEntity<?> hasVoted() {
        
        return voteRepo.getByUserInCurrentPoll(AuthorizedUser.get())
                       .map(this::getOkResponse)
                       .orElse(new ResponseEntity<>(NOT_FOUND));
    }
    
    private ResponseEntity<Resource<Vote.Brief>> getOkResponse(Vote.Brief vote) {
        
        Resource<Vote.Brief> resource = new Resource<>(vote);
        resource.add(entityLinks.linkForSingleResource(vote.getRestaurant()).withRel("restaurant"));
        resource.add(entityLinks.linkForSingleResource(vote.getMenu()).withRel("menu"));
        
        return ResponseEntity.ok(resource);
    }
}
