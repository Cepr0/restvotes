package restvotes.rest.view;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author Cepro, 2017-01-11
 */
@Relation(value = "poll", collectionRelation = "polls")
@JsonPropertyOrder({"date", "finished", "current", "_embedded"})
public class PollView extends Poll implements Poll.Detailed {

    @Setter
    @Getter
    @JsonInclude(NON_NULL)
    @JsonProperty("current")
    private Boolean current;
    
    // To get embedded Menu array in json output
    // "_embedded"
    @JsonUnwrapped
    @Getter
    @Setter
    private Resources<Resource<Menu.Detailed>> menuResources;
    
    public PollView(Poll poll, LocalDate curPollDate) {
    
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
    
    @JsonIgnore
    @Override
    public List<Menu> getMenus() {
        return super.getMenus();
    }
}
