package restvotes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Restaurant;

/**
 * Repository to manage {@link Menu} instances.
 *
 * @author Cepro, 2017-01-01
 */
@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
@RepositoryRestResource
public interface MenuRepo extends JpaRepository<Menu, Long> {
    
    /**
     * Get a menu list of the given {@link Restaurant}
     * <p>Used in {@link restvotes.rest.controller.RestaurantController}</p>
     *
     * @param restaurant a given {@link Restaurant}
     * @param pageable a Pageable parameter
     * @return a pageable {@link Menu} list of the given {@link Restaurant}
     */
    @RestResource(exported = false)
    Page<Menu> findByRestaurantOrderByIdDesc(Restaurant restaurant, Pageable pageable);
    
    // TODO Make exported getByRestaurantNameLikeIgnoreCase
    // TODO Make by Items search
    // TODO Make contextual search?
}
