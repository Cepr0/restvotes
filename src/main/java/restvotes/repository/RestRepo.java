package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import restvotes.domain.entity.Restaurant;

/**
 * @author Cepro, 2017-01-01
 */
@RepositoryRestResource
public interface RestRepo extends JpaRepository<Restaurant, Long> {
    // TODO Add RestaurantController and getMenusByRestaurantId() for api/restaurants/{id}/menus
}
