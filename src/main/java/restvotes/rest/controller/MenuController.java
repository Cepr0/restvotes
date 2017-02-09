package restvotes.rest.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Vote;
import restvotes.service.VoteService;
import restvotes.util.MessageService;
import restvotes.util.exception.NotFoundException;

import static org.springframework.http.HttpStatus.OK;
import static restvotes.util.LinksHelper.*;

/**
 * @author Cepro, 2017-01-08
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/menus/{id}")
public class MenuController {
    
    private final @NonNull MessageService msgService;
    
    private final @NonNull VoteService voteService;

    @PutMapping("/vote")
    public ResponseEntity<?> submitVote(@PathVariable("id") Menu menu) {
        
        if (menu == null) {
            throw new NotFoundException(msgService.userMessage("menu.not_found"));
        }
        
        Vote vote = voteService.submitVote(menu);
        
        // If submitting was successful
        if (vote != null) {
            
            return new ResponseEntity<>(
                    new Resource<Vote.Registered>(
                            vote::getRegistered,
                            getRestaurantLink(vote.getRestaurant()),
                            getMenuLink(vote.getMenu()),
                            getPollLink(vote.getPoll())
                    ), OK);
            
        } else { // If current unfinished Poll is not found
            throw new NotFoundException(msgService.userMessage("poll.current_not_found"));
        }
    }
}
