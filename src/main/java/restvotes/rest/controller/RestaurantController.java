package restvotes.rest.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Restaurant;
import restvotes.repository.MenuRepo;
import restvotes.util.LinksHelper;
import restvotes.util.MessageHelper;
import restvotes.util.exception.NotFoundException;

/**
 * Controller to handle query for the restaurant related menus.
 * <p>An example how to deal with related objects without specifying their relations in entities classes</p>
 *
 * @author Cepro, 2017-01-22
 */
@Slf4j
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/restaurants/{id}")
public class RestaurantController {
    
    private final @NonNull LinksHelper links;
    
    private final @NonNull MessageHelper msgHelper;
    
    private final @NonNull PagedResourcesAssembler<Menu> pagedResourcesAssembler;
    
    private final @NonNull MenuRepo menuRepo;
    
    /**
     * Get {@link Menu} list of the specified Restaurant
     * <p>(see a corresponding Menus link in output of {@code /api/restaurants/{id}})</p>
     *
     * @param restaurant the specified Restaurant
     * @param pageable Pageable parameter
     * @return Pageable Menu list
     */
    @GetMapping("/menus")
    public ResponseEntity<?> getMenus(@PathVariable("id") Restaurant restaurant, Pageable pageable) {
        
        if (restaurant == null) {
            throw new NotFoundException(msgHelper.userMessage("restaurant.not_found"));
        }
        
        Page<Menu> menus = menuRepo.findByRestaurantOrderByIdDesc(restaurant, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(menus,
                menu -> new Resource<>(menu, links.getMenuLinks(menu, true))));
    }
    
    /**
     * Add new {@link Menu} to specified Restaurant
     * <p>(see a corresponding Menus link in output of {@code /api/restaurants/{id}})</p>
     * @param restaurant the specified Restaurant
     * @param menu a {@link Menu} to saved
     * @return a saved {@link Menu}
     */
    @Transactional
    @PostMapping("/menus")
    public ResponseEntity<?> addMenu(@PathVariable("id") Restaurant restaurant, @RequestBody Menu menu) {
        if (restaurant == null) {
            throw new NotFoundException(msgHelper.userMessage("restaurant.cannot_be_null"));
        }
        
        if (menu == null) {
            throw new NotFoundException(msgHelper.userMessage("menu.cannot_be_null"));
        }
        
        Menu savedMenu = menuRepo.saveAndFlush(menu.setRestaurant(restaurant));
        return ResponseEntity.ok(new Resource<>(savedMenu, links.getMenuLinks(savedMenu, true)));
    }
}
