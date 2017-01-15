package restvotes.web.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.*;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;

import java.time.LocalDateTime;
import java.util.Optional;

import static restvotes.util.LinksHelper.*;

/**
 * @author Cepro, 2017-01-08
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/menus/{id}")
public class MenuController {
    
    private final @NonNull VoteRepo voteRepo;
    
    private final @NonNull PollRepo pollRepo;
    
    @Transactional
    @PutMapping("/vote")
    public ResponseEntity<?> submitVote(@PathVariable("id") Menu menu) {
        
        if (menu == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        ResponseEntity<?> result;
        
        Optional<Poll> pollOptional = pollRepo.getCurrent();
        if (pollOptional.isPresent()) {
            
            Poll poll = pollOptional.get();
            if (!poll.getFinished()) {
                
                User user = AuthorizedUser.get();
                Restaurant restaurant = menu.getRestaurant();
                
                Optional<Vote> voteOptional = voteRepo.findByPollAndUser(poll, user);
                if (voteOptional.isPresent()) {
                    
                    Vote vote = voteOptional.get();
                    vote.setMenu(menu);
                    vote.setRestaurant(restaurant);
                    vote.setRegistered(LocalDateTime.now());
                    
                    Vote updated = voteRepo.save(vote);
                    // TODO Handle an exception here if Vote didn't save
                    result = new ResponseEntity<>(getMenuBriefResource(updated), HttpStatus.OK);
                    
                } else {
                    Vote created = voteRepo.save(new Vote(poll, menu, restaurant, user));
                    // TODO Handle an exception here if Vote didn't save
                    result = new ResponseEntity<>(getMenuBriefResource(created), HttpStatus.CREATED);
                }
            } else {
                result = new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        } else {
            result = new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        return result;
    }
    
    private Resource<Vote.Brief> getMenuBriefResource(final Vote vote) {
        
        Resource<Vote.Brief> resource = new Resource<>(new Vote.Brief() {
            @Override
            public LocalDateTime getRegistered() {
                return vote.getRegistered();
            }
            
            @Override
            public Restaurant getRestaurant() {
                return null;
            }
            
            @Override
            public Menu getMenu() {
                return null;
            }
        });
        
        resource.add(
                getRestaurantLink(vote.getRestaurant()),
                getMenuLink(vote.getMenu()),
                getPollLink(vote.getPoll()));
        
        return resource;
    }
}
