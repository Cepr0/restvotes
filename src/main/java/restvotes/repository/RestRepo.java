package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Restaurant;

import java.util.List;

/**
 * Repository to manage {@link Restaurant} instances.
 * @author Cepro, 2017-01-01
 */
@RepositoryRestResource
public interface RestRepo extends JpaRepository<Restaurant, Long> {

    // TODO Make multi-params search http://stackoverflow.com/a/38652548/5380322
    // TODO Make 'contextual' search?
    
    /**
     * Find {@link Restaurant}s by their name part
     * @param name name part
     * @return {@link Restaurant} list
     */
    @RestResource(path = "byName", rel = "byName")
    List<Restaurant> findByNameIgnoreCaseContainingOrderByNameAsc(@Param("name") String name);
    
    /**
     * Find {@link Restaurant}s by their address part
     * @param address address part
     * @return {@link Restaurant} list
     */
    @RestResource(path = "byAddress", rel = "byAddress")
    List<Restaurant> findByAddressIgnoreCaseContainingOrderByNameAsc(@Param("address") String address);
}
