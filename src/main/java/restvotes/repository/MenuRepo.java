package restvotes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Restaurant;

/**
 * @author Cepro, 2017-01-01
 */
@RepositoryRestResource(excerptProjection = Menu.Detailed.class)
public interface MenuRepo extends JpaRepository<Menu, Long> {
    
    @RestResource(exported = false)
    @Query("select m from Menu m where m.restaurant = ?1 order by m.id desc")
    Page<Menu.Detailed> getByRestaurant(Restaurant restaurant, Pageable pageable);
}
