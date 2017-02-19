package restvotes.rest.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * DTO for {@link Poll} in brief form (to use in Poll list)
 *
 * @author Cepro, 2017-01-28
 */
@Relation(value = "poll", collectionRelation = "polls")
@JsonInclude(NON_NULL)
@JsonPropertyOrder({"date", "finished", "current"})
public class PollBriefView implements Poll.Brief {
    
    @JsonInclude(NON_NULL)
    private Boolean current = null;
    
    @JsonIgnore
    private Poll.Brief poll;
        
    public PollBriefView(Poll.Brief poll, LocalDate curPollDate) {
        this.poll = poll;
        current = curPollDate != null && curPollDate.isEqual(getDate());
    }
    
    @JsonProperty("date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Override
    public LocalDate getDate() {
        return poll.getDate();
    }
    
    @JsonProperty("finished")
    @Override
    public Boolean getFinished() {
        return poll.getFinished();
    }
    
    @Override
    public Boolean getCurrent() {
        return current;
    }
    
    @JsonIgnore
    @Override
    public Menu getWinner() {
        return poll.getWinner();
    }
}
