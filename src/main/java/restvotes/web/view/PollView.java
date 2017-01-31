package restvotes.web.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static restvotes.util.LinksHelper.getMenuLinks;
import static restvotes.util.LinksHelper.getPollViewLinks;

/**
 * @author Cepro, 2017-01-11
 */
@Relation(value = "poll", collectionRelation = "polls")
@JsonPropertyOrder({"date", "finished", "current", "_embedded"})
public class PollView extends Poll implements Poll.Detailed {
    
    // To get embedded Menu array in json output
    // "_embedded"
    @JsonUnwrapped
    private Resources<Resource<Menu.Detailed>> menuResources;
    
    public PollView(Poll poll, Long chosenMenuId, Map<Long, Integer> ranks, LocalDate curPollDate) {
    
        setData(
                poll.getDate(),
                poll.getFinished(),
                poll.getMenus(),
                poll.getWinner(),
                chosenMenuId,
                ranks,
                curPollDate
        );
    }
    
    private void setData(LocalDate date, Boolean finished, List<Menu> menus, Menu winner, Long chosenMenuId, Map<Long, Integer> ranks, LocalDate curPollDate) {

        super.setId(date);
        super.setFinished(finished);
        
        List<Resource<Menu.Detailed>> content = new ArrayList<>();
        for (Menu menu : menus) {
            MenuView menuView = new MenuView(menu, chosenMenuId, ranks, winner != null ? winner.getId() : null);
            super.getMenus().add(menuView);
            
            // making menu resource and adding its links
            content.add(new Resource<>(menuView, getMenuLinks(menuView, finished)));
        }
        
        menuResources = new Resources<>(content, getPollViewLinks(date, chosenMenuId, winner));
    
        setCurrent(curPollDate != null && curPollDate.isEqual(getDate()));
    }
    
    @JsonIgnore
    @Override
    public LocalDate getId() {
        return super.getId();
    }

    @JsonProperty("date")
    @Override
    public LocalDate getDate() {
        return super.getDate();
    }
    
    @JsonProperty("finished")
    @Override
    public Boolean getFinished() {
        return super.getFinished();
    }
    
    @JsonProperty("current")
    @Override
    public Boolean getCurrent() {
        return super.getCurrent();
    }
    
    @JsonIgnore
    @Override
    public List<Menu> getMenus() {
        return super.getMenus();
    }
    
    public Resources<Resource<Menu.Detailed>> getMenuResources() {
        return this.menuResources;
    }
}
