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
import restvotes.to.MenuView;

import java.util.ArrayList;
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
    
            Collection<Resource<Menu.Detailed>> content = resources.getContent();
    
            Optional<Vote.Brief> voteOptional = voteRepo.getByUserInCurrentPoll(AuthorizedUser.get());
    
            final long voteMenuId = voteOptional.isPresent() ? voteOptional.get().getMenu().getId() : 0;
    
            Collection<Resource<Menu.Detailed>> resultContent = new ArrayList<>();
    
            for (Resource<Menu.Detailed> resource : content) {
                resultContent.add(fillResource(resource, voteMenuId));
            }
    
            Resources<Resource<Menu.Detailed>> resultResources = new Resources<>(resultContent, resources.getLinks());
            resultResources.add(entityLinks.linkFor(User.class).slash("choice").withRel("choice"));
            return resultResources;
        }
    
        private Resource<Menu.Detailed> fillResource(Resource<Menu.Detailed> resource, final long voteMenuId) {
        
            Menu.Detailed menu = resource.getContent();
            long menuId = menu.getId();

            MenuView menuView = new MenuView(menu).setChosen(voteMenuId == menuId);
        
            Resource<Menu.Detailed> result = new Resource<>(menuView, resource.getLinks());
        
            result.add(entityLinks.linkForSingleResource(Menu.class, menuId).slash("vote").withRel("vote"));
        
            return result;
        }
    }
}
