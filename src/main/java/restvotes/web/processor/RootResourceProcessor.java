package restvotes.web.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;

import static java.time.format.DateTimeFormatter.ISO_DATE;

/**
 * @author Cepro, 2017-01-13
 */
@Component
@RequiredArgsConstructor
public class RootResourceProcessor implements ResourceProcessor<RepositoryLinksResource> {
    
    private final @NonNull RepositoryEntityLinks entityLinks;
    
    private final @NonNull PollRepo pollRepo;
    
    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        pollRepo.getCurrent().ifPresent(poll -> {
            String curPollDateStr = poll.getId().format(ISO_DATE);
            resource.add(entityLinks.linkFor(Poll.class).slash(curPollDateStr).slash("menus").withRel("currentMenus"));
            resource.add(entityLinks.linkFor(Poll.class).slash(curPollDateStr).slash("/?projection=brief").withRel("currentPoll"));
        });
        return resource;
    }
}
