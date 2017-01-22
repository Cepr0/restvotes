package restvotes.web.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Restaurant;
import restvotes.repository.MenuRepo;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author Cepro, 2017-01-22
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/restaurants/{id}")
public class RestaurantController {
    
    private final @NonNull PagedResourcesAssembler<Menu.Detailed> pagedResourcesAssembler;
    
    private final @NonNull MenuRepo menuRepo;
    
    @GetMapping("/menus")
    ResponseEntity<?> getMenus(@PathVariable("id")Restaurant restaurant, Pageable pageable) {
    
        if (restaurant == null) {
            // TODO Replace with exception?
            return new ResponseEntity(NOT_FOUND);
        }
        
        // TODO Find a better solution without pages
        Page<Menu.Detailed> menus = menuRepo.getByRestaurant(restaurant, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(menus));
    }
}
