package restvotes.rest.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.rest.view.PollBriefView;
import restvotes.rest.view.PollView;
import restvotes.util.AuthorizedUser;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static restvotes.util.LinksHelper.*;

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
            
            pagedResources.add(getPollProfileLink(), getPollSearchLink());
            return pagedResources;
        }
    }

    @Component
    public class PollBriefResourceProcessor implements ResourceProcessor<Resource<Poll.Brief>> {

        @Override
        public Resource<Poll.Brief> process(Resource<Poll.Brief> resource) {
            Poll.Brief poll = resource.getContent();
    
            Optional<Poll> pollOptional = pollRepo.getCurrent();
            LocalDate curPollDate = pollOptional.isPresent() ? pollOptional.get().getDate() : null;
    
            PollBriefView pollBriefView = new PollBriefView(poll, curPollDate);
            Resource<Poll.Brief> viewResource = new Resource<>(pollBriefView, getPollSelfLink(poll.getDate()));
            
            if (poll.getFinished()) {

                // Determining the winner
                Menu winner = poll.getWinner();
                if (winner != null) {
                    viewResource.add(getWinnerLink(poll, winner));
                }
            }
            return viewResource;
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
    
            Optional<Poll> pollOptional = pollRepo.getCurrent();
            LocalDate curPollDate = pollOptional.isPresent() ? pollOptional.get().getDate() : null;
    
            return new Resource<>(new PollView(poll, chosenMenuId, ranks, curPollDate));
        }
    }
}
