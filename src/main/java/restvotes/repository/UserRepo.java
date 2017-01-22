package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author Cepro, 2017-01-02
 */
@RepositoryRestResource
public interface UserRepo extends JpaRepository<User, Long> {
    
    // TODO Add search for Users by: role, enabled
    
    @RestResource(path = "byEmail", rel = "byEmail")
    Optional<User> findByEmail(@Param("email") String email);
    
    @RestResource(path = "byName", rel = "byName")
    List<User> findByNameLikeIgnoreCase(@Param("name") String name);
}
