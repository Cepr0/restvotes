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
    
    @RestResource(path = "byName", rel = "byName")
    List<Restaurant> findByNameLikeIgnoreCaseOrderByNameAsc(@Param("name") String name);
    
    @RestResource(path = "byAddress", rel = "byAddress")
    List<Restaurant> findByAddressLikeIgnoreCaseOrderByNameAsc(@Param("address") String name);
}
