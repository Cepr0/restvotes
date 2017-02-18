package restvotes.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.base.DateId;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

/**
 * Main entity in the project - defines the voting in the given day.
 * <p>Only one Poll per day.</p>
 * @author Cepro, 2017-01-01
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "polls")
public class Poll extends DateId {
    
    /**
     * The mark - is Poll finished or not
     */
    private boolean finished = false;
    
    /**
     * Menu list of given Poll
     */
    @ManyToMany(cascade = {PERSIST, MERGE})
    private List<Menu> menus = new ArrayList<>();
    // TODO Think about use @PreRemove to unlink Menus then deleting 'empty' Poll - http://stackoverflow.com/a/14911910/5380322
    
    /**
     * Winner of the Poll - only for finished Polls
     */
    @RestResource(exported = false)
    @JsonIgnore
    @ManyToOne(optional = true)
    private Menu winner;
    
    public Poll(@NonNull List<Menu> menus) {
        setMenus(menus);
    }
    
    public Poll(@NonNull LocalDate date, @NonNull List<Menu> menus) {
        super(date);
        setMenus(menus);
    }
    
    public Poll setMenus(@NonNull List<Menu> menus) {
        this.menus = menus;
        return this;
    }
    
    public Boolean getFinished() {
        return finished;
    }
    
    /**
     * A brief view of Poll used in the Poll list
     */
//    @Projection(name = "brief", types = {Poll.class})
    public interface Brief {
        LocalDate getDate();

        Boolean getFinished();
    
        @JsonInclude(NON_NULL)
        Boolean getCurrent();
        
        @RestResource(exported = false)
        @JsonIgnore
        Menu getWinner();
    }
    
    /**
     * A detailed view of Poll with unwrapped Menu and Restaurant data
     */
    public interface Detailed {
        LocalDate getDate();

        Boolean getFinished();

        @JsonInclude(NON_NULL)
        Boolean getCurrent();

        List<Menu> getMenus();
        
        @RestResource(exported = false)
        @JsonIgnore
        Menu getWinner();
    }
}
