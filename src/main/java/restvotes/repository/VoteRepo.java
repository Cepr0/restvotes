package restvotes.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.User;
import restvotes.domain.entity.Vote;

import java.util.Optional;

/**
 * @author Cepro, 2017-01-02
 */
@RepositoryRestResource(exported = false)
public interface VoteRepo extends CrudRepository<Vote, Long> {
    
    @RestResource(exported = false)
    Optional<Vote> findByPollAndUser(Poll poll, User user);
    
    @SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @RestResource(exported = false)
    @Query("select v from Vote v join v.user u where u = ?1 and v.poll.finished = false")
    Optional<Vote.Brief> getByUserInCurrentPoll(User user);
}
