package restvotes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
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
    
    @RestResource(exported = false)
    @Query("select p from Poll p order by p.date desc")
    // @Cacheable("polls")
    Page<Poll.Brief> getAll(Pageable pageable);
    
    @RestResource(path = "current", rel = "current")
    // @Cacheable("polls")
    default Optional<Poll> getCurrent() {
        // If unfinished polls are present then get first one, else get last Poll until now
        return getFirstUnfinished()
                .map(Optional::of)
                .orElse(this.getLast(LocalDate.now()));
    }

    @RestResource(exported = false)
    @Query(value = "select * from polls p where p.date <= :date order by p.date desc limit 1", nativeQuery = true)
    // @Cacheable("polls")
    Optional<Poll> getLast(@Param("date") LocalDate date);
    
    @RestResource(exported = false)
    @Query(value = "select * from polls p where p.finished = false order by p.date asc limit 1", nativeQuery = true)
    // @Cacheable("polls")
    Optional<Poll> getFirstUnfinished();
    
    // // @Cacheable(value = "polls")
    // List<Poll> findByFinishedIsFalseOrderByDateAsc();
    
    @RestResource(path = "byDate", rel = "byDate")
    // @Cacheable("polls")
    Optional<Poll> findByDate(@Param("date") LocalDate date);
    
    @RestResource(exported = false)
    @Query("select p from Poll p where p.finished = true and p.winner is null")
    // @Cacheable("polls")
    List<Poll> getFinishedWithoutWinner();
    
    @RestResource(exported = false)
    @Modifying(clearAutomatically = true)
    @Query("update Poll p set p.finished = true where p.finished = false and p.date <= ?1")
    // @Cachevict(value = "polls", allEntries = true)
    int closeUntil(LocalDate until);
    
    @RestResource(exported = false)
    @Modifying(clearAutomatically = true)
    @Query("delete from Poll p where p.winner is null and p.finished = true")
    // @Cachevict(value = "polls", allEntries = true)
    int deleteFinishedAndWithoutVotes();
    
    @RestResource(exported = false)
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from polls_menus where poll_date in (select p.date from polls p where p.winner_id is null and p.finished = true)", nativeQuery = true)
    void unlinkMenusFromFinishedAndWithoutVotes();
}
