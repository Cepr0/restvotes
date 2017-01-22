package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Restaurant;

import java.util.List;

/**
 * @author Cepro, 2017-01-01
 */
@RepositoryRestResource
public interface RestRepo extends JpaRepository<Restaurant, Long> {
    // TODO Add RestaurantController and getMenusByRestaurantId() for api/restaurants/{id}/menus
    
    @RestResource(path = "byName", rel = "byName")
    List<Restaurant> findByNameLikeIgnoreCase(@Param("name") String name);
    
    @RestResource(path = "byAddress", rel = "byAddress")
    List<Restaurant> findByAddressLikeIgnoreCase(@Param("address") String name);
}
