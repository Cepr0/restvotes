package restvotes.web.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.User;
import restvotes.repository.UserRepo;
import restvotes.web.view.UserProfile;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Cepro, 2017-01-24
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/users")
public class UserController {
    
    private final @NonNull UserRepo userRepo;
    
    @GetMapping("/profile")
    ResponseEntity<?> get() {

        return userRepo.findById(AuthorizedUser.get().getId())
                .map(profile -> ok(new Resource<>(new UserProfile(profile))))
                .orElse(notFound().build());
    }

    @PostMapping("/profile")
    @Transactional
    ResponseEntity<?> signUp(@RequestBody UserProfile profile) {
        return ok(new Resource<>(userRepo.saveAndFlush(new User(profile.getName(), profile.getEmail(), profile.getPassword()))));
    }

    // TODO Make '/api/signUp' request / create RootController
    // TODO Make UserView with only name/email/password fields
    // TODO Change output of 'users/profile' to UserView Resource
    // TODO Maybe move 'users/profile' to '/api/userProfile'
}
