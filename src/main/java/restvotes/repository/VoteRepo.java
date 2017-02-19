package restvotes.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.User;
import restvotes.domain.entity.Vote;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

/**
 * Repository to manage {@link Vote} instances.
 * @author Cepro, 2017-01-02
 */
@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
@RepositoryRestResource(exported = false)
public interface VoteRepo extends CrudRepository<Vote, Long> {
    
    @RestResource(exported = false)
    Optional<Vote> findByPollAndUser(Poll poll, User user);
    
    @RestResource(exported = false)
    @Query("select v from Vote v join v.user u where u = ?1 and v.poll.date = ?2")
    Optional<Vote> getByUserAndDate(User user, LocalDate date);
    
    // http://stackoverflow.com/a/20127110/5380322
    @RestResource(exported = false)
    // @Query("select v.menu as menu, count(v) as rank from Vote v where v.poll.date = ?1 group by v.menu order by count(v) desc")
    @Query("select m as menu, count(v) as rank from Vote v inner join v.menu m where v.poll.date = ?1 group by m order by count(v) desc")
    List<Vote.Rank> getRanksByDate(LocalDate date);
    
    @RestResource(exported = false)
    default Map<Long, Integer> getMenuAndRankParesByDate(LocalDate date) {
        return getRanksByDate(date).stream().collect(toMap(Vote.Rank::getId, Vote.Rank::getRank));
    }
    
    @RestResource(exported = false)
    Integer countByPoll(Poll poll);
    
    Optional<Vote> findFirstByMenu(Menu menu);
}
