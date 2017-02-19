package restvotes.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
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
    
    Optional<Vote> findByPollAndUser(Poll poll, User user);
    
    @Query("select v from Vote v join v.user u where u = ?1 and v.poll.date = ?2")
    Optional<Vote> getByUserAndDate(User user, LocalDate date);
    
    /**
     * Get 'rank' i.e. votes count per {@link Menu} of the {@link Poll} for specified date
     * @param date date of {@link Poll}
     * @return {@link Vote.Rank} list
     */
    // http://stackoverflow.com/a/20127110/5380322
    // @Query("select v.menu as menu, count(v) as rank from Vote v where v.poll.date = ?1 group by v.menu order by count(v) desc") - doesn't work with MySQL
    @Query("select m as menu, count(v) as rank from Vote v inner join v.menu m where v.poll.date = ?1 group by m order by count(v) desc")
    List<Vote.Rank> getRanksByDate(LocalDate date);
    
    /**
     * Transform {@link Vote.Rank} list to convenient {@code Map<Long, Integer>} ({@link Menu} id and votes count)
     * from {@link VoteRepo#getRanksByDate(LocalDate)} by specified {@link Poll} date
     *
     * @param pollDate specified {@link Poll} date
     * @return {@code Map<Long, Integer>} of {@link Menu} id and votes count
     */
    default Map<Long, Integer> getMenuAndRankParesByDate(LocalDate pollDate) {
        return getRanksByDate(pollDate).stream().collect(toMap(Vote.Rank::getId, Vote.Rank::getRank));
    }
    
    /**
     * Get total number of {@link Vote}s by specified {@link Poll}
     * @param poll specified {@link Poll}
     * @return total number of {@link Vote}s
     */
    Integer countByPoll(Poll poll);
    
    /**
     * Get one {@link Vote} of specified {@link Menu}
     * @param menu specified {@link Menu}
     * @return {@link Vote} or not
     */
    Optional<Vote> findFirstByMenu(Menu menu);
}
