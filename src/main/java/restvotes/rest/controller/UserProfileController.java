package restvotes.rest.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import restvotes.domain.entity.User;
import restvotes.repository.UserRepo;
import restvotes.rest.view.UserProfile;
import restvotes.util.AuthorizedUser;
import restvotes.util.ErrorAssembler;
import restvotes.util.LinksHelper;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Controller to handle {@link UserProfile} related requests
 * @author Cepro, 2017-01-24
 */
@RequiredArgsConstructor
@RestController
// @BasePathAwareController
@RequestMapping("${spring.data.rest.basePath}/userProfile")
public class UserProfileController {
    
    private final @NonNull LinksHelper links;
    
    private final @NonNull UserRepo userRepo;
    
    private final @NonNull ErrorAssembler assembler;
    
    // http://stackoverflow.com/a/39083293/5380322
    // http://blog.codeleak.pl/2013/09/request-body-validation-in-spring-mvc-3.2.html
    // http://www.baeldung.com/exception-handling-for-rest-with-spring
    // https://g00glen00b.be/validating-the-input-of-your-rest-api-with-spring/
    
    /**
     * Get profile of current {@link User}
     * @return {@link UserProfile} response
     */
    @GetMapping
    public ResponseEntity<?> get() {

        return userRepo.findById(AuthorizedUser.get().getId())
                .map(profile -> ok(new Resource<>(new UserProfile(profile), links.getUserProfileLink())))
                .orElse(notFound().build());
    }
    
    /**
     * Sing up routine
     * @param profile {@link UserProfile}
     * @param bindingResult {@link BindingResult} used to validate the input
     * @return {@link UserProfile} of a new {@link User}
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> signUp(@Valid @RequestBody UserProfile profile, BindingResult bindingResult) {
    
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(assembler.errorMsg(bindingResult));
        }
        
        User user = userRepo.saveAndFlush(new User(profile.getName(), profile.getEmail(), profile.getPassword()));
        return ok(new Resource<>(new UserProfile(user), links.getUserProfileLink()));
    }
    
    /**
     * Updating {@link UserProfile}
     * @param profile {@link UserProfile}
     * @param bindingResult {@link BindingResult} used to validate the input
     * @return updated {@link UserProfile}
     */
    @RequestMapping(method = {PUT, PATCH})
    @Transactional
    public ResponseEntity<?> update(@Valid @RequestBody UserProfile profile, BindingResult bindingResult) {
    
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(assembler.errorMsg(bindingResult));
        }
    
        return userRepo.findById(AuthorizedUser.get().getId())
                .map(user -> {
                    userRepo.saveAndFlush(user.update(profile.getName(), profile.getEmail(), profile.getPassword()));
                    return ok(new Resource<>(new UserProfile(user), links.getUserProfileLink()));
                })
                .orElse(notFound().build());
    }
    
    // @DeleteMapping
    // @Transactional
    // public ResponseEntity<?> delete() {
    //     if (userRepo.disable(AuthorizedUser.get().getId()) > 0) {
    //
    //         return ok().build();
    //     } else {
    //         return notFound().build();
    //     }
    // }
}
