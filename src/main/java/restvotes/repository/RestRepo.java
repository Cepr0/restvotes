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

    // TODO http://stackoverflow.com/a/38652548/5380322
    // TODO Make contextual search

    @RestResource(path = "byName", rel = "byName")
    List<Restaurant> findByNameIgnoreCaseContainingOrderByNameAsc(@Param("name") String name);
    
    @RestResource(path = "byAddress", rel = "byAddress")
    List<Restaurant> findByAddressIgnoreCaseContainingOrderByNameAsc(@Param("address") String name);
}
