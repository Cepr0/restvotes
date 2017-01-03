package restvotes.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import restvotes.domain.entity.Vote;

/**
 * @author Cepro, 2017-01-02
 */
@RepositoryRestResource(excerptProjection = Vote.Brief.class)
public interface VoteRepo extends CrudRepository<Vote, Long> {
}
