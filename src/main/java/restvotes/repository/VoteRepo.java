package restvotes.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.User;
import restvotes.domain.entity.Vote;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Cepro, 2017-01-02
 */
@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
@RepositoryRestResource(exported = false)
public interface VoteRepo extends CrudRepository<Vote, Long> {
    
    @RestResource(exported = false)
    Optional<Vote> findByPollAndUser(Poll poll, User user);
    
    
    @RestResource(exported = false)
    @Query("select v from Vote v join v.user u where u = ?1 and v.poll.finished = false")
    Optional<Vote.Brief> getByUserInCurrentPoll(User user);
    
    @RestResource(exported = false)
    @Query("select v.menu as menu, count(v) as rank from Vote v where v.poll.date = ?1 group by v.menu")
    List<Vote.Rank> getRanksByDate(LocalDate date);
}
