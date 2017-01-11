package restvotes.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.Poll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author Cepro, 2017-01-11
 */
@Getter
@Setter
@Relation(value = "poll", collectionRelation = "polls")
@JsonInclude(NON_NULL)
public class PollView extends Poll implements Identifiable<LocalDate> {
    
    private static final EmbeddedWrappers wrappers = new EmbeddedWrappers(true);
    
    private LocalDate date;
    
    private Boolean finished;
    
    @JsonUnwrapped
    private Resources<EmbeddedWrapper> embeddeds;
    
    
    public PollView(Poll poll) {
        date = poll.getDate();
        finished = poll.isFinished();
        List<EmbeddedWrapper> listEmbeddeds = new ArrayList<>();
        poll.getMenus().forEach(menu -> listEmbeddeds.add(wrappers.wrap(menu)));
        embeddeds = new Resources<>(listEmbeddeds);
    }
}
