package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import restvotes.repository.UserRepo;

/**
 * @author Cepro, 2017-01-06
 */
@RequiredArgsConstructor
@RepositoryRestController
public class RequestController {
    
    private final @NonNull UserRepo userRepo;
    
    @GetMapping("/users/{id}/hasVotedToday")
    public @ResponseBody ResponseEntity<?> hasVotedToDayById(@PathVariable("id") Long id) {
        
        Boolean hasVoted = userRepo.hasVotedTodayById(id) != 0;
    
        Resource<Boolean> resource = new Resource<>(hasVoted);
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/users/hasVotedToday/{email}")
    public @ResponseBody ResponseEntity<?> hasVotedToDayByEmail(@PathVariable("email") String email) {
        
        Boolean hasVoted = userRepo.hasVotedTodayByEmail(email) != 0;
        
        Resource<Boolean> resource = new Resource<>(hasVoted);
        return ResponseEntity.ok(resource);
    }
}
