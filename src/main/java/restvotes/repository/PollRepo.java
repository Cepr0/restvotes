package restvotes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
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
    @Query("select p from Poll p where p.finished = false")
    Optional<Poll> getCurrent();
    
    @RestResource(exported = false)
    @Query("select p from Poll p where p.finished = false")
    Optional<Poll.Detailed> getCurrentDetailed();
}
