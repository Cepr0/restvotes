package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.User;

import java.time.LocalDate;

/**
 * @author Cepro, 2017-01-02
 */
@RepositoryRestResource
public interface UserRepo extends JpaRepository<User, Long> {
    
    @RestResource(exported = false)
    @Query("select v from Vote v join v.user u where v.poll = current_date and u.email = ?1")
    User hasVotedTodayByEmail(String email);
    
    default int hasVotedToday(String email) {
        return hasVotedByEmail(LocalDate.now(), email);
    }
    
    @RestResource(exported = false)
    @Query(value = "select count(v.id) from votes v join users u on u.id = v.user_id and u.email = ?2 where v.poll_date = ?1", nativeQuery = true)
    int hasVotedByEmail(LocalDate date, String email);
    
    @RestResource(exported = false)
    @Query("select count(v) as count from Vote v join v.user u where u.id = :id and v.poll = current_date")
    int hasVotedTodayById(@Param("id") Long id);
}
