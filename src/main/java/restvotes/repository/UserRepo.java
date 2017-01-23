package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import restvotes.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author Cepro, 2017-01-02
 */
@RepositoryRestResource
public interface UserRepo extends JpaRepository<User, Long> {
    
    // TODO Add search for Users by: role, enabled
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RestResource(path = "byEmail", rel = "byEmail")
    Optional<User> findByEmail(@Param("email") String email);
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RestResource(path = "byName", rel = "byName")
    List<User> findByNameLikeIgnoreCaseOrderByNameAsc(@Param("name") String name);
    
    @RestResource(exported = false)
    @Query("select u from User u where u.email = ?1 and u.enabled = true")
    Optional<User> findEnabledByEmail(@Param("email") String email);
}
