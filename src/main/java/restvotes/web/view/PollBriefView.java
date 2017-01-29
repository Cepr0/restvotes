package restvotes.web.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.Poll;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author Cepro, 2017-01-28
 */
@Relation(value = "poll", collectionRelation = "polls")
@JsonInclude(NON_NULL)
@JsonPropertyOrder({"date", "finished", "current"})
public class PollBriefView extends Poll implements Poll.Brief {
    
}
