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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Restaurant;
import restvotes.repository.MenuRepo;
import restvotes.util.exception.NotFoundException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static restvotes.util.LinksHelper.getMenuLinks;

/**
 * @author Cepro, 2017-01-22
 */
@Slf4j
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/restaurants/{id}")
public class RestaurantController {
    
    private final @NonNull PagedResourcesAssembler<Menu> pagedResourcesAssembler;
    
    private final @NonNull MenuRepo menuRepo;
    
    @GetMapping("/menus")
    ResponseEntity<?> getMenus(@PathVariable("id")Restaurant restaurant, Pageable pageable) {
    
        if (restaurant == null) {
            throw new NotFoundException("restaurant.not_found");
        }
        
        // TODO Make it without pages?
        Page<Menu> menus = menuRepo.findByRestaurantOrderByIdDesc(restaurant, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(menus,
                menu -> new Resource<>(menu, getMenuLinks(menu, true))));
    }
    
    @Transactional
    @RequestMapping(path = "/menus", method = {POST, PUT})
    public ResponseEntity<?> putMenu(@PathVariable("id")Restaurant restaurant, @RequestBody Menu menu) {
        if (restaurant == null) {
            throw new NotFoundException("restaurant.cannot_be_null");
        }
        
        if (menu == null) {
            throw new NotFoundException("menu.cannot_be_null");
        }
        // TODO Move this to Service?
        Menu savedMenu = menuRepo.saveAndFlush(menu.setRestaurant(restaurant));
        return ResponseEntity.ok(new Resource<>(savedMenu, getMenuLinks(savedMenu, true)));
    }
}
