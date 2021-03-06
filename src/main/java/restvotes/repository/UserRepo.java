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
 * Repository to manage {@link User} instances.
 * @author Cepro, 2017-01-02
 */
@RepositoryRestResource
public interface UserRepo extends JpaRepository<User, Long> {
    
    // TODO http://stackoverflow.com/a/38652548/5380322
    // TODO Make 'contextual' search?

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RestResource(path = "byRole", rel = "byRole")
    List<User> findByRole(@Param("role") User.Role role);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RestResource(path = "disabled", rel = "disabled")
    List<User> findByEnabledFalse();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RestResource(path = "enabled", rel = "enabled")
    List<User> findByEnabledTrue();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RestResource(path = "byEmail", rel = "byEmail")
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * Find {@link User}s by name part
     * @param name name part
     * @return {@link User} list
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RestResource(path = "byName", rel = "byName")
    List<User> findByNameIgnoreCaseContainingOrderByNameAsc(@Param("name") String name);
    
    /** Find enabled {@link User}s only by email
     * @param email specified email
     * @return {@link User} found or not
     */
    @RestResource(exported = false)
    @Query("select u from User u where u.email = ?1 and u.enabled = true")
    Optional<User> findEnabledByEmail(@Param("email") String email);

    @RestResource(exported = false)
    @Query("select u from User u where u.id = ?1")
    Optional<User> findById(Long id);
    
    // @Modifying(clearAutomatically = true)
    // @Query("update User u set u.enabled = false where u.id = ?1")
    // @Transactional
    // int disable(Long id);
    //
    // @Modifying(clearAutomatically = true)
    // @Query("update User u set u.enabled = true where u.id = ?1")
    // @Transactional
    // int enable(Long id);
}
