package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import restvotes.domain.entity.Menu;

/**
 * @author Cepro, 2017-01-01
 */
@RepositoryRestResource//(excerptProjection = Menu.Detailed.class)
public interface MenuRepo extends JpaRepository<Menu, Long> {
}
