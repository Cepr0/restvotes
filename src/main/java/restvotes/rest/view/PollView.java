package restvotes.rest.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Setter;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Cepro, 2017-01-11
 */
@Relation(value = "poll", collectionRelation = "polls")
@JsonPropertyOrder({"date", "finished", "current", "_embedded"})
public class PollView extends Poll implements Poll.Detailed {
    
    // To get embedded Menu array in json output
    // "_embedded"
    @JsonUnwrapped
    @Setter
    private Resources<Resource<Menu.Detailed>> menuResources;
    
    public PollView(Poll poll, Long chosenMenuId, Map<Long, Integer> ranks, LocalDate curPollDate) {
    
        super.setId(poll.getDate());
        super.setFinished(poll.getFinished());
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
