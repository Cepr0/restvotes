package restvotes.web.controller;

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
import restvotes.service.MenuService;

import static org.springframework.http.HttpStatus.*;
import static restvotes.util.LinksHelper.*;

/**
 * @author Cepro, 2017-01-08
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/menus/{id}")
public class MenuController {
    
    private final @NonNull MenuService menuService;

    @PutMapping("/vote")
    public ResponseEntity<?> submitVote(@PathVariable("id") Menu menu) {
        
        if (menu == null) {
            return new ResponseEntity(NOT_FOUND);
        }
        
        Vote vote = menuService.submitVote(menu);
        
        if (vote != null) {
            
            return new ResponseEntity<>(
                    new Resource<Vote.Registered>(
                            vote::getRegistered,
                            getRestaurantLink(vote.getRestaurant()),
                            getMenuLink(vote.getMenu()),
                            getPollLink(vote.getPoll())
                    ), OK);
            
        } else { // If current unfinished Poll is not found
            return new ResponseEntity(FORBIDDEN);
        }
    }
}
