package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import restvotes.domain.entity.User;

/**
 * @author Cepro, 2017-01-02
 */
@RepositoryRestResource
public interface UserRepo extends JpaRepository<User, Long> {
}
