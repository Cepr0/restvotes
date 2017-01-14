package restvotes.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static restvotes.to.LinksHelper.getMenuViewLinks;
import static restvotes.to.LinksHelper.getPollViewLinks;

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
    
    @JsonIgnore
    private List<Menu.Detailed> menus = new ArrayList<>();
    
    // To get embedded Menu array in json output
    @JsonUnwrapped
    private Resources<Resource<Menu.Detailed>> menuResources;
    
    public PollView(Poll.Detailed poll, Long chosenMenuId, Map<Long, Integer> ranks) {
        date = poll.getDate();
        finished = poll.getFinished();
    
        List<Resource<Menu.Detailed>> content = new ArrayList<>();
        
        for (Menu.Detailed menu : poll.getMenus()) {
            MenuView menuView = new MenuView(menu, chosenMenuId, ranks);
            menus.add(menuView);
            content.add(new Resource<>(menuView, getMenuViewLinks(menuView)));
        }
        
        menuResources = new Resources<>(content, getPollViewLinks(poll, chosenMenuId));
    }
    
    @JsonIgnore
    @Override
    public LocalDate getId() {
        return date;
    }
}
