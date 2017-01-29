package restvotes.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.base.DateId;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

/**
 * @author Cepro, 2017-01-01
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "polls")
public class Poll extends DateId {
    
    private boolean finished = false;
    
    @JsonInclude(NON_NULL)
    @Transient
    private Boolean current = null;
    
    @ManyToMany(cascade = {PERSIST, MERGE})
    private List<Menu> menus = new ArrayList<>();
    // TODO Think about use @PreRemove to unlink Menus then deleting 'empty' Poll - http://stackoverflow.com/a/14911910/5380322
    
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
    
    public Boolean getCurrent() {
        return current;
    }
    
//    @Projection(name = "brief", types = {Poll.class})
    public interface Brief {
        LocalDate getDate();
        Boolean getFinished();
    
        @JsonInclude(NON_NULL)
        Boolean getCurrent();
        
        @RestResource(exported = false)
        @JsonIgnore
        Menu getWinner();
    
        @RestResource(exported = false)
        @JsonIgnore
        List<Menu> getMenus();
    }
    
    public interface Detailed {
        LocalDate getDate();
        Boolean getFinished();
        List<Menu> getMenus();
        
        @RestResource(exported = false)
        @JsonIgnore
        Menu getWinner();
    }
}
