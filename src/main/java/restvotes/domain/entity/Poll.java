package restvotes.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import restvotes.domain.base.DateId;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    
    @ManyToMany(cascade = {PERSIST, MERGE})
    private List<Menu> menus = new ArrayList<>();
    
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
    
//    @Projection(name = "brief", types = {Poll.class})
    public interface Brief {
        LocalDate getDate();
        Boolean getFinished();
        @JsonIgnore
        Menu getWinner();
        @JsonIgnore
        List<Menu> getMenus();
    }
    
    public interface Detailed {
        LocalDate getDate();
        Boolean getFinished();
        List<Menu> getMenus();
        @JsonIgnore Menu getWinner();
    }
}
