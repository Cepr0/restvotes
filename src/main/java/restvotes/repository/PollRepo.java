package restvotes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Cepro, 2017-01-01
 */
@RepositoryRestResource(excerptProjection = Poll.Detailed.class)
public interface PollRepo extends JpaRepository<Poll, LocalDate> {
    
    @RestResource(exported = false)
    @Query("select p from Poll p order by p.date desc")
    Page<Poll.Brief> getAll(Pageable pageable);
    
    @RestResource(exported = false)
    @Query("select p from Poll p where p.finished = false order by p.date asc")
    Page<Poll> getUnfinished(Pageable pageable);
    
    @RestResource(exported = false)
    @Query("select p from Poll p where p.finished = false order by p.date asc")
    Page<Poll.Detailed> getUnfinishedDetailed(Pageable pageable);
    
    // http://stackoverflow.com/a/22472888/5380322
    @RestResource(exported = false)
    default Optional<Poll> getCurrent() {
        Page<Poll> polls = getUnfinished(new PageRequest(0, 1));
        List<Poll> pollList = polls.getContent();
        return !pollList.isEmpty() ? Optional.of(pollList.get(0)) : Optional.empty();
    }
    
    @RestResource(exported = false)
    default Optional<Poll.Detailed> getCurrentDetailed() {
        Page<Poll.Detailed> polls = getUnfinishedDetailed(new PageRequest(0, 1));
        List<Poll.Detailed> pollList = polls.getContent();
        return !pollList.isEmpty() ? Optional.of(pollList.get(0)) : Optional.empty();
    }
    
    @RestResource(exported = false)
    @Modifying(clearAutomatically = true)
    @Query("update Poll p set p.finished = true where p.finished = false and p.date <= ?1")
    int disableUntil(LocalDate until);
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = "menus") // Кастыль!!!
    @Query("select p from Poll p where p.date <= :date order by p.date desc")
    Page<Poll> getPrevious(@Param("date") LocalDate date, Pageable page);
    
    @RestResource(exported = false)
    default Optional<Poll> getLast(LocalDate date) {
        Page<Poll> polls = getPrevious(date, new PageRequest(0, 1));
        List<Poll> pollList = polls.getContent();
        return !pollList.isEmpty() ? Optional.of(pollList.get(0)) : Optional.empty();
    }

    @EntityGraph(attributePaths = "menus")  // Кастыль!!!
    Poll findByDate(LocalDate date);
}
