package restvotes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Cepro, 2017-01-01
 */
@RepositoryRestResource//(excerptProjection = Poll.Brief.class)
public interface PollRepo extends JpaRepository<Poll, LocalDate> {
    
    @Query("select p from Poll p order by p.date desc")
    Page<Poll.Brief> getAll(Pageable pageable);
    
    @Query("select p from Poll p where p.date = current_date")
    Optional<Poll> getCurrent();
}
