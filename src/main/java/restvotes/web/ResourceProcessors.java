package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.*;
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
        public PagedResources<Resource<Poll.Brief>> process(PagedResources<Resource<Poll.Brief>> resource) {
            Collection<Resource<Poll.Brief>> polls = resource.getContent();
            for (Resource<Poll.Brief> pollResource : polls) {
                Poll.Brief poll = pollResource.getContent();
                if (!poll.getFinished()) {
                    resource.add(entityLinks.linkFor(Poll.class).slash(poll.getDate().format(ISO_DATE)).slash("menus").withRel("current"));
                    resource.add(entityLinks.linkFor(Poll.class).slash("current").withRel("current"));
                }
            }
            return resource;
        }
    }
    
    @Component
    public class ResourceSupportProcessor implements ResourceProcessor<ResourceSupport> {
        
        @Override
        public ResourceSupport process(ResourceSupport resource) {
            return resource;
        }
    }
    
    @Component
    public class PollResourceProcessor implements ResourceProcessor<Resource<? extends Poll>> {
        
        // http://stackoverflow.com/a/29039089/5380322
        // https://github.com/spring-projects/spring-hateoas/issues/270
        @Override
        public Resource<? extends Poll> process(Resource<? extends Poll> resource) {
            // Poll source = resource.getContent();
            // PollView poll = new PollView(source);
            // Resource<PollView> pollResource = new Resource<>(poll);
            //
            // return pollResource;
            return resource;
        }
    }
    
    @Component
    public class AnyResourceProcessor implements ResourceProcessor<Resource<?>> {

        @Override
        public Resource<?> process(Resource<?> resource) {
            return resource;
        }
    }

    @Component
    public class AnyResourcesProcessor implements ResourceProcessor<Resources<?>> {

        @Override
        public Resources<?> process(Resources<?> resource) {
            return resource;
        }
    }

    @Component
    public class AnyPagedResourcesProcessor implements ResourceProcessor<PagedResources<?>> {

        @Override
        public PagedResources<?> process(PagedResources<?> resource) {
            return resource;
        }
    }

    @Component
    public class MenuDetailedResourcesProcessor implements ResourceProcessor<Resources<Resource<Menu.Detailed>>> {
        
        @Override
        public Resources<Resource<Menu.Detailed>> process(Resources<Resource<Menu.Detailed>> resource) {
    
            Collection<Resource<Menu.Detailed>> content = resource.getContent();
    
            Optional<Vote.Brief> voteOptional = voteRepo.getByUserInCurrentPoll(AuthorizedUser.get());
    
            final long voteMenuId = voteOptional.isPresent() ? voteOptional.get().getMenu().getId() : 0;
    
            Collection<Resource<Menu.Detailed>> resultContent = new ArrayList<>();
    
            for (Resource<Menu.Detailed> menuResource : content) {
                resultContent.add(fillResource(menuResource, voteMenuId));
            }
    
            Resources<Resource<Menu.Detailed>> resultResources = new Resources<>(resultContent, resource.getLinks());
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
