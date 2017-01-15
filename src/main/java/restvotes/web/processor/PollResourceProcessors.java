package restvotes.web.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.Poll;
import restvotes.repository.VoteRepo;
import restvotes.web.view.PollView;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import static restvotes.web.LinksHelper.getCurrentPollLink;
import static restvotes.web.LinksHelper.getPollLink;

/**
 * @author Cepro, 2017-01-13
 */
@RequiredArgsConstructor
@Component
public class PollResourceProcessors {
    
    private final @NonNull VoteRepo voteRepo;
    
    @Component
    public class PollBriefPagedResourcesProcessor implements ResourceProcessor<PagedResources<Resource<Poll.Brief>>> {
        
        @Override
        public PagedResources<Resource<Poll.Brief>> process(PagedResources<Resource<Poll.Brief>> resource) {
        
            Collection<Resource<Poll.Brief>> polls = resource.getContent();
            for (Resource<Poll.Brief> pollResource : polls) {
                Poll.Brief poll = pollResource.getContent();
                
                pollResource.add(getPollLink(poll.getDate()));

                if (!poll.getFinished()) {
                    resource.add(getCurrentPollLink(poll.getDate()));
                } else {
                    // TODO Add winner
                }
            }
            return resource;
        }
    }
    
    // @Component
    // public class PollDetailedResourceProcessor implements ResourceProcessor<Resource<Poll.Detailed>> {
    //
    //     @Override
    //     public Resource<Poll.Detailed> process(Resource<Poll.Detailed> resource) {
    //
    //         Poll.Detailed poll = resource.getContent();
    //         LocalDate pollDate = poll.getDate();
    //
    //         Long chosenMenuId = voteRepo.getByUserAndDate(AuthorizedUser.get(), pollDate)
    //                                     .map(v -> v.getMenu().getId())
    //                                     .orElse(null);
    //
    //         Map<Long, Integer> ranks = voteRepo.getMenuAndRankParesByDate(pollDate);
    //
    //         return new Resource<>(new PollView(poll, chosenMenuId, ranks));
    //     }
    // }
    //
    @Component
    public class PollResourceProcessor implements ResourceProcessor<Resource<Poll>> {
        
        @Override
        public Resource<Poll> process(Resource<Poll> resource) {
            
            Poll poll = resource.getContent();
            LocalDate pollDate = poll.getDate();
            
            Long chosenMenuId = voteRepo.getByUserAndDate(AuthorizedUser.get(), pollDate)
                                        .map(v -> v.getMenu().getId())
                                        .orElse(null);
            
            Map<Long, Integer> ranks = voteRepo.getMenuAndRankParesByDate(pollDate);
            
            return new Resource<>(new PollView(poll, chosenMenuId, ranks));
        }
    }
}
