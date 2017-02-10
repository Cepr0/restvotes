package restvotes.rest.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import restvotes.domain.entity.User;
import restvotes.repository.UserRepo;
import restvotes.rest.view.UserProfile;
import restvotes.util.AuthorizedUser;
import restvotes.util.LinksHelper;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Cepro, 2017-01-24
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("${spring.data.rest.basePath}/userProfile")
public class UserProfileController {
    
    private final @NonNull LinksHelper links;
    
    private final @NonNull UserRepo userRepo;
    
    @GetMapping
    public ResponseEntity<?> get() {

        return userRepo.findById(AuthorizedUser.get().getId())
                .map(profile -> ok(new Resource<>(new UserProfile(profile), links.getUserProfileLink())))
                .orElse(notFound().build());
    }
    
    @PostMapping
    @Transactional
    public ResponseEntity<?> signUp(@RequestBody UserProfile profile) {
        
        User user = userRepo.saveAndFlush(new User(profile.getName(), profile.getEmail(), profile.getPassword()));
        return ok(new Resource<>(new UserProfile(user), links.getUserProfileLink()));
    }
    
    @RequestMapping(method = {PUT, PATCH})
    @Transactional
    public ResponseEntity<?> update(@RequestBody UserProfile profile) {
        
        return userRepo.findById(AuthorizedUser.get().getId())
                .map(user -> {
                    userRepo.saveAndFlush(user.update(profile.getName(), profile.getEmail(), profile.getPassword()));
                    return ok(new Resource<>(new UserProfile(user), links.getUserProfileLink()));
                })
                .orElse(notFound().build());
    }
    
    @DeleteMapping
    @Transactional
    public ResponseEntity<?> delete() {
        if (userRepo.disable(AuthorizedUser.get().getId()) > 0) {
            
            return ok().build();
        } else {
            return notFound().build();
        }
    }
}
