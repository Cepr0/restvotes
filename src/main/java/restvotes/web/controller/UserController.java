package restvotes.web.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.User;
import restvotes.repository.UserRepo;

/**
 * @author Cepro, 2017-01-24
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/users")
public class UserController {
    
    private final @NonNull UserRepo userRepo;
    
    @GetMapping("/profile")
    ResponseEntity<?> getCurrent(PersistentEntityResourceAssembler assembler) {
        User user = userRepo.findOne(AuthorizedUser.get().getId());
        return ResponseEntity.ok(assembler.toFullResource(user));
    }
    
    // TODO Make '/api/signUp' request / create RootController
    // TODO Make UserView with only name/email/password fields
    // TODO Change output of 'users/profile' to UserView Resource
    // TODO Maybe move 'users/profile' to '/api/userProfile'
}
