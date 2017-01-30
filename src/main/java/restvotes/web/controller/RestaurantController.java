package restvotes.web.controller;

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
import restvotes.util.exception.NotFoundException;
import restvotes.web.view.MenuView;

import static restvotes.util.LinksHelper.getMenuViewLinks;

/**
 * @author Cepro, 2017-01-22
 */
@Slf4j
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/restaurants/{id}")
public class RestaurantController {
    
    private final @NonNull PagedResourcesAssembler<Menu.Detailed> pagedResourcesAssembler;
    
    private final @NonNull MenuRepo menuRepo;
    
    @GetMapping("/menus")
    ResponseEntity<?> getMenus(@PathVariable("id")Restaurant restaurant, Pageable pageable) {
    
        if (restaurant == null) {
            throw new NotFoundException("restaurant.not_found");
        }
        
        // TODO Find a solution without pages?
        Page<Menu.Detailed> menus = menuRepo.getByRestaurant(restaurant, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(menus));
    }
    
    @Transactional
    @PostMapping("/menus")
    @PutMapping("/menus")
    public ResponseEntity<?> putMenu(@PathVariable("id")Restaurant restaurant, @RequestBody Menu menu) {
        if (restaurant == null) {
            throw new NotFoundException("restaurant.cannot_be_null");
        }
        
        if (menu == null) {
            throw new NotFoundException("menu.cannot_be_null");
        }
        // TODO Move this to Service?
        Menu savedMenu = menuRepo.saveAndFlush(menu.setRestaurant(restaurant));
        MenuView menuView = new MenuView(savedMenu);
        return ResponseEntity.ok(new Resource<>(menuView, getMenuViewLinks(menuView, true)));
    }
}
