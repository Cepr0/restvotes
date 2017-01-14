package restvotes.web.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.Poll;
import restvotes.repository.VoteRepo;
import restvotes.to.PollView;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_DATE;

/**
 * @author Cepro, 2017-01-13
 */
@RequiredArgsConstructor
@Component
public class PollResourceProcessors {
    
    private final @NonNull RepositoryEntityLinks entityLinks;
    
    private final @NonNull VoteRepo voteRepo;
    
    @Component
    public class PollBriefPagedResourcesProcessor implements ResourceProcessor<PagedResources<Resource<Poll.Brief>>> {
        
        @Override
        public PagedResources<Resource<Poll.Brief>> process(PagedResources<Resource<Poll.Brief>> resource) {
        
            Collection<Resource<Poll.Brief>> polls = resource.getContent();
            for (Resource<Poll.Brief> pollResource : polls) {
                Poll.Brief poll = pollResource.getContent();
                
                String dateStr = poll.getDate().format(ISO_DATE);
                LinkBuilder menusLink = entityLinks.linkFor(Poll.class).slash(dateStr).slash("menus");
                
                if (!poll.getFinished()) {
                    resource.add(menusLink.withRel("currentMenus"));
                    resource.add(entityLinks.linkFor(Poll.class).slash(dateStr).slash("/?projection=brief").withRel("currentPoll"));
                } else {
                    // TODO Add winner
                }

                pollResource.add(menusLink.withRel("menus"));
            }
            return resource;
        }
    }
    
    @Component
    public class PollBriefResourceProcessor implements ResourceProcessor<Resource<Poll.Detailed>> {
    
        @Override
        public Resource<Poll.Detailed> process(Resource<Poll.Detailed> resource) {
    
            Poll.Detailed poll = resource.getContent();
            LocalDate pollDate = poll.getDate();

            Long chosenMenuId = voteRepo.getByUserAndDate(AuthorizedUser.get(), pollDate)
                                        .map(v -> v.getMenu().getId())
                                        .orElse(null);
    
            Map<Long, Integer> ranks = voteRepo.getMenuAndRankParesByDate(pollDate);
    
            return new Resource<>(new PollView(poll, chosenMenuId, ranks));
        }
    }
    
}
