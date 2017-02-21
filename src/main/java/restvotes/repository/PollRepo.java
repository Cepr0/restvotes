package restvotes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Vote;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository to manage {@link Poll} instances.
 * @author Cepro, 2017-01-01
 */
@SuppressWarnings({"SpringDataJpaMethodInconsistencyInspection", "SpringCacheAnnotationsOnInterfaceInspection"})
@RepositoryRestResource
public interface PollRepo extends JpaRepository<Poll, LocalDate> {
    
    // Caching
    // http://stackoverflow.com/a/26283080/5380322
    // https://spring.io/guides/gs/caching/
    // http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html
    
    // Predefine sorting solution?
    // http://stackoverflow.com/a/40825434/5380322
    
    /**
     * A 'main' {@link Poll} list which is output in 'brief' form in descending order.
     * <p>Used in {@link restvotes.rest.controller.PollController}</p>
     * @param pageable a pageable parameter
     * @return a pageable {@link Poll} brief list
     */
    @RestResource(exported = false)
    @Query("select p from Poll p order by p.date desc")
    // @Cacheable("polls")
    Page<Poll.Brief> getAll(Pageable pageable);
    
    /**
     * Return the current {@link Poll}
     * <p>Current {@link Poll} is the first unfinished Poll or last finished one</p>
     * @return current {@link Poll} or not
     */
    @RestResource(path = "current", rel = "current")
    // @Cacheable("polls")
    default Optional<Poll> getCurrent() {
        // If unfinished polls are present then get first one, else get last Poll until now
        return getFirstUnfinished()
                .map(Optional::of)
                .orElse(this.getLast(LocalDate.now()));
    }
    
    /**
     * Get last {@link Poll} by specified date
     * @param date specified date
     * @return found {@link Poll} or not
     */
    @RestResource(exported = false)
    @Query(value = "select * from polls p where p.date <= :date order by p.date desc limit 1", nativeQuery = true)
    // @Cacheable("polls")
    Optional<Poll> getLast(@Param("date") LocalDate date);
    
    /**
     * Get first unfinished {@link Poll}
     * @return first unfinished {@link Poll} or not
     */
    @RestResource(exported = false)
    @Query(value = "select * from polls p where p.finished = false order by p.date asc limit 1", nativeQuery = true)
    // @Cacheable("polls")
    Optional<Poll> getFirstUnfinished();
    
    // // @Cacheable(value = "polls")
    // List<Poll> findByFinishedIsFalseOrderByDateAsc();
    
    /**
     * Find {@link Poll} by specified date
     * @param date specified date
     * @return found {@link Poll} or not
     */
    @RestResource(path = "byDate", rel = "byDate")
    // @Cacheable("polls")
    Optional<Poll> findByDate(@Param("date") LocalDate date);
    
    /**
     * Find {@link Poll}s which are finished but where has not been set a winner
     * @return {@link Poll} list
     */
    @RestResource(exported = false)
    @Query("select p from Poll p where p.finished = true and p.winner is null")
    // @Cacheable("polls")
    List<Poll> getFinishedWithoutWinner();
    
    /**
     * Close (i.e. finished) all unfinished {@link Poll}s by specified date
     * @param until specified date
     * @return number of closed {@link Poll}s
     */
    @RestResource(exported = false)
    @Modifying(clearAutomatically = true)
    @Query("update Poll p set p.finished = true where p.finished = false and p.date <= ?1")
    // @Cachevict(value = "polls", allEntries = true)
    int closeUntil(LocalDate until);
    
    /**
     * Delete finished but without any {@link Vote}s (i.e. 'empty') {@link Poll}s
     * @return number of deleted Polls
     */
    @RestResource(exported = false)
    @Modifying(clearAutomatically = true)
    @Query("delete from Poll p where p.winner is null and p.finished = true")
    // @Cachevict(value = "polls", allEntries = true)
    int deleteFinishedAndWithoutVotes();
    
    /**
     * Delete {@link Menu}s related with 'empty' {@link Poll}s
     * <p>Use before {@link PollRepo#deleteFinishedAndWithoutVotes()} just in the same transaction</p>
     * <p>(Perhaps it's worth to use @PreRemove method in {@link Poll} entity...)</p>
     */
    @RestResource(exported = false)
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from polls_menus where poll_date in (select p.date from polls p where p.winner_id is null and p.finished = true)", nativeQuery = true)
    void unlinkMenusFromFinishedAndWithoutVotes();
}
