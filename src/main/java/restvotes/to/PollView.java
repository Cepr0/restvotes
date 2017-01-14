package restvotes.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Cepro, 2017-01-11
 */
@Getter
@Setter
@Relation(value = "poll", collectionRelation = "polls")
// @JsonInclude(NON_NULL)
public class PollView implements Poll.Detailed, Identifiable<LocalDate> {
    
    private LocalDate date;
    
    private Boolean finished;
    
    private List<Menu.Detailed> menus = new ArrayList<>();
    
    public PollView(Poll.Detailed poll, Long chosenMenuId, Map<Long, Integer> ranks) {
        date = poll.getDate();
        finished = poll.getFinished();
        poll.getMenus().forEach(menu -> menus.add(new MenuView(menu, chosenMenuId, ranks)));
        
    }
    
    @JsonIgnore
    @Override
    public LocalDate getId() {
        return date;
    }
    
    // private static final EmbeddedWrappers wrappers = new EmbeddedWrappers(true);
    //
    // @JsonUnwrapped
    // private Resources<EmbeddedWrapper> embeddeds;
    
    // public PollView(Poll poll) {
    //     List<EmbeddedWrapper> listEmbeddeds = new ArrayList<>();
    //     poll.getMenus().forEach(menu -> listEmbeddeds.add(wrappers.wrap(menu)));
    //     embeddeds = new Resources<>(listEmbeddeds);
    // }
}
