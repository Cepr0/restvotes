package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.User;
import restvotes.domain.entity.Vote;
import restvotes.repository.VoteRepo;

import java.util.Collection;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_DATE;

/**
 * @author Cepro, 2016-12-21
 */
@Component
@RequiredArgsConstructor
public class ResourceProcessors {
    
    private final @NonNull RepositoryEntityLinks entityLinks;
    
    private final @NonNull VoteRepo voteRepo;
    
    @Component
    public class PollBriefPagedResourceProcessor implements ResourceProcessor<PagedResources<Resource<Poll.Brief>>> {
    
        @Override
        public PagedResources<Resource<Poll.Brief>> process(PagedResources<Resource<Poll.Brief>> pagedResources) {
            Collection<Resource<Poll.Brief>> polls = pagedResources.getContent();
            for (Resource<Poll.Brief> resource : polls) {
                Poll.Brief poll = resource.getContent();
                if (!poll.getFinished()) {
                    pagedResources.add(entityLinks.linkFor(Poll.class).slash(poll.getDate().format(ISO_DATE)).slash("menus").withRel("current"));
                    pagedResources.add(entityLinks.linkFor(Poll.class).slash("current").withRel("current"));
                }
            }
            return pagedResources;
        }
    }
    
    @Component
    public class MenuDetailedResourcesProcessor implements ResourceProcessor<Resources<Resource<Menu.Detailed>>> {
        
        @Override
        public Resources<Resource<Menu.Detailed>> process(Resources<Resource<Menu.Detailed>> resources) {
    
            Collection<Resource<Menu.Detailed>> menus = resources.getContent();
    
            Optional<Vote.Brief> voteOptional = voteRepo.getByUserInCurrentPoll(AuthorizedUser.get());
            long voteMenuId = 0;
            if (voteOptional.isPresent()) {
                voteMenuId = voteOptional.get().getMenu().getId();
            }
    
            for (Resource<Menu.Detailed> resource : menus) {
                Menu.Detailed menu = resource.getContent();
                Long menuId = menu.getId();
                resource.add(entityLinks.linkForSingleResource(Menu.class, menuId).slash("vote").withRel("vote"));
    
                if (voteMenuId == menuId) {
                }
            }
    
            resources.add(entityLinks.linkFor(User.class).slash("choice").withRel("choice"));
            return resources;
        }
    }
}
