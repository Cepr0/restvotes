package restvotes.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.rest.core.config.Projection;
import restvotes.domain.base.LongId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

/**
 * Defines Vote entity.
 * <p>The object is persisted when a user votes for the chosen restaurant menu.
 * <p>Only one vote of the user per day
 * @author Cepro, 2016-11-26
 */
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "votes")
public class Vote extends LongId {

    @JsonProperty(access = WRITE_ONLY)
    @ManyToOne(optional = false)
    @NaturalId
    private final Poll poll;

    @JsonProperty(access = WRITE_ONLY)
    @ManyToOne(optional = false)
    private final Menu menu;

    @JsonProperty(access = WRITE_ONLY)
    @ManyToOne(optional = false)
    private final Restaurant restaurant;

    @JsonProperty(access = WRITE_ONLY)
    @ManyToOne(optional = false)
    @NaturalId
    private final User user;
    
    @Column(columnDefinition = "timestamp default now()", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private final LocalDateTime registered;
    
    public Vote() {
        this(null, null, null, null, LocalDateTime.now());
    }
    
    public Vote(Poll poll, Menu menu, Restaurant restaurant, User user) {
        this(poll, menu, restaurant, user, LocalDateTime.now());
    }

    @Projection(name = "brief", types = Vote.class)
    public interface Brief {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime getRegistered();
    }
}
