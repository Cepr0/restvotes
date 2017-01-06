package restvotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.entity.User;
import restvotes.domain.entity.Vote;

import java.time.LocalDateTime;

/**
 * @author Cepro, 2017-01-02
 */
@RepositoryRestResource
public interface UserRepo extends JpaRepository<User, Long> {
    
    @RestResource(exported = false)
    @Query("select count(v) as count from Vote v join v.user u where u.email = :email and v.poll = current_date")
    int hasVotedTodayByEmail(@Param("email") String email);
    
    @RestResource(exported = false)
    @Query("select count(v) as count from Vote v join v.user u where u.id = :id and v.poll = current_date")
    int hasVotedTodayById(@Param("id") Long id);
}
