package restvotes.web.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.web.view.PollView;

import java.time.LocalDate;
import java.util.Map;

import static restvotes.util.LinksHelper.getCurrentPollLink;
import static restvotes.util.LinksHelper.getWinnerLink;

/**
 * @author Cepro, 2017-01-13
 */
@RequiredArgsConstructor
@Component
public class PollResourceProcessors {
    
    private final @NonNull PollRepo pollRepo;
    
    private final @NonNull VoteRepo voteRepo;
    
    @Component
    public class PollBriefPagedResourcesProcessor implements ResourceProcessor<PagedResources<Resource<Poll.Brief>>> {
        
        @Override
        public PagedResources<Resource<Poll.Brief>> process(PagedResources<Resource<Poll.Brief>> pagedResources) {
    
            pollRepo.getCurrent()
                    .ifPresent(poll -> pagedResources.add(getCurrentPollLink()));
            
            //pagedResources.add(getPollProfileLink(), getPollSearchLink());
            
            return pagedResources;
        }
    }

    @Component
    public class PollBriefResourceProcessor implements ResourceProcessor<Resource<Poll.Brief>> {

        @Override
        public Resource<Poll.Brief> process(Resource<Poll.Brief> resource) {
            Poll.Brief poll = resource.getContent();
            // pollResource.add(getPollSelfLink(poll.getDate()), getPollLink(poll.getDate()));

            if (poll.getFinished()) {

                // Determining the winner
                Menu winner = poll.getWinner();
                if (winner != null) {
                    resource.add(getWinnerLink(poll, winner));
                }
            }
            return resource;
        }
    }
    
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
