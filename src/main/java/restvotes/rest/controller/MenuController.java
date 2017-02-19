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
import restvotes.util.LinksHelper;
import restvotes.util.MessageHelper;
import restvotes.util.exception.NotFoundException;

import static org.springframework.http.HttpStatus.OK;

/**
 * Controller to handle voting for chosen {@link Menu}
 * @author Cepro, 2017-01-08
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/menus/{id}")
public class MenuController {
    
    private final @NonNull LinksHelper links;
    
    private final @NonNull MessageHelper msgHelper;
    
    private final @NonNull VoteService voteService;
    
    /**
     * Submit a vote for chosen {@link Menu}
     * @param menu chosen {@link Menu}
     * @return a response with the registered {@link Vote}
     */
    @PutMapping("/vote")
    public ResponseEntity<?> submitVote(@PathVariable("id") Menu menu) {
        
        if (menu == null) {
            throw new NotFoundException(msgHelper.userMessage("menu.not_found"));
        }
        
        Vote vote = voteService.submitVote(menu);
        
        // If submitting was successful
        if (vote != null) {
            
            return new ResponseEntity<>(
                    new Resource<Vote.Registered>(
                            vote::getRegistered,
                            links.getRestaurantLink(vote.getRestaurant()),
                            links.getMenuLink(vote.getMenu()),
                            links.getPollLink(vote.getPoll())
                    ), OK);
            
        } else { // If current unfinished Poll is not found
            throw new NotFoundException(msgHelper.userMessage("poll.current_not_found"));
        }
    }
}
