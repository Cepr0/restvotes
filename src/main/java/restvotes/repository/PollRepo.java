package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;

/**
 * @author Cepro, 2017-01-01
 */
@RepositoryRestResource(excerptProjection = Poll.Brief.class)
public interface PollRepo extends JpaRepository<Poll, LocalDate> {
}
