package restvotes.web.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Vote;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.to.MenuView;

import java.time.LocalDate;
import java.util.*;

import static java.time.LocalDate.parse;
import static java.util.stream.Collectors.toMap;

/**
 * @author Cepro, 2017-01-13
 */
@Component
@RequiredArgsConstructor
public class MenuResourceProcessor {
    
    private final @NonNull RepositoryEntityLinks entityLinks;
    
    private final @NonNull VoteRepo voteRepo;
    
    private final @NonNull PollRepo pollRepo;
    
    /**
     * The {@link ResourceProcessor} implementation for {@link Menu.Detailed} Resource to process requests like this:
     * http://localhost:8080/api/polls/{date}/menus
     */
    @Component
    public class MenuDetailedResourcesProcessor implements ResourceProcessor<Resources<Resource<Menu.Detailed>>> {
        
        @Override
        public Resources<Resource<Menu.Detailed>> process(Resources<Resource<Menu.Detailed>> resource) {
    
            Poll curPoll = pollRepo.getCurrent().orElse(null);
    
            Collection<Resource<Menu.Detailed>> content = resource.getContent();

            long voteMenuId = 0;
            LocalDate pollDate = null;
            Map<Long, Integer> menuRanks = null;
            try {
                // Get the current Poll date from Menu self link
                pollDate = getPollDate(resource);
    
                // Get Menu ID of the User vote in the current Poll date
                Optional<Vote.Brief> voteOptional = voteRepo.getByUserAndDate(AuthorizedUser.get(), pollDate);
                voteMenuId = voteOptional.isPresent() ? voteOptional.get().getMenu().getId() : 0;
                
                // Get Map<Long, Integer> for every Menu and its rank in the current Poll date
                List<Vote.Rank> ranks = voteRepo.getRanksByDate(pollDate);
                menuRanks = ranks.stream().collect(toMap(Vote.Rank::getId, Vote.Rank::getRank));
            } catch (Exception ignored) {
            }
            
            Collection<Resource<Menu.Detailed>> resultContent = new ArrayList<>();
            
            // Fill the result content
            for (Resource<Menu.Detailed> menuResource : content) {
                
                Resource<Menu.Detailed> filledResource = fillResource(menuResource, voteMenuId, menuRanks);
                if (curPoll != null && pollDate != null && curPoll.getDate().isEqual(pollDate)) {
                    filledResource.add(entityLinks
                            .linkForSingleResource(Menu.class, menuResource.getContent().getId())
                            .slash("vote")
                            .withRel("vote"));
                }

                resultContent.add(filledResource);
            }
            
            Resources<Resource<Menu.Detailed>> resultResources = new Resources<>(resultContent, resource.getLinks());
            
            // Add link to User choice
            if (pollDate != null) {
                resultResources.add(entityLinks.linkForSingleResource(Poll.class, pollDate).slash("choice").withRel("choice"));
            }
            
            return resultResources;
        }
    
        /**
         * Get the Poll date from Menu self link of {@link Menu.Detailed} Resource
         * @param resource {@link Menu.Detailed} Resource
         * @return Poll date
         */
        private LocalDate getPollDate(Resources<Resource<Menu.Detailed>> resource) {
            LocalDate date;
            String selfStr = resource.getLink("self").getHref();
            // Matcher m = Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(selfStr);
            // m.find();
            // LocalDate date = parse(m.group(0));
            String[] parts = selfStr.split("/");
            date = parse(parts[parts.length - 2]);
            return date;
        }
    
        /**
         * Fill {@link Menu.Detailed} Resource with new data: 'rank' for this Menu and 'chosen' if
         * current Menu ID is equals with Menu ID of the User's current choice
         *
         * @param resource {@link Menu.Detailed} Resource that must be filled
         * @param voteMenuId Menu ID of the User's current choice
         * @param menuRanks {@link Map} of the ranks for all Menus in the Poll
         * @return The new filled Resource
         */
        private Resource<Menu.Detailed> fillResource(Resource<Menu.Detailed> resource, long voteMenuId, Map<Long, Integer> menuRanks) {
            
            Menu.Detailed menu = resource.getContent();
            long menuId = menu.getId();
            
            MenuView menuView = new MenuView(menu)
                    .setChosen(voteMenuId == menuId)
                    .setRank(menuRanks != null? menuRanks.getOrDefault(menuId, 0) : null);
    
            return new Resource<>(menuView, resource.getLinks());
        }
    }
}
