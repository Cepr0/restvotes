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

import java.time.LocalDate;
import java.util.*;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.stream.Collectors.toMap;

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
                LinkBuilder menusLink = entityLinks.linkFor(Poll.class).slash(poll.getDate().format(ISO_DATE)).slash("menus");
                if (!poll.getFinished()) {
                    resource.add(menusLink.withRel("current.menu"));
                    resource.add(entityLinks.linkFor(Poll.class).slash("current?projection=brief").withRel("current.poll"));
                } else {
                    // TODO Add winner
                }
                pollResource.add(menusLink.withRel("menus"));
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
    
            Map<Long, Integer> menuRanks = null;
            try {
                String selfStr = resource.getLink("self").getHref();
                // Matcher m = Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(selfStr);
                // m.find();
                // LocalDate date = parse(m.group(0));
                String[] parts = selfStr.split("/");
                LocalDate date = parse(parts[parts.length - 2]);
                List<Vote.Rank> ranks = voteRepo.getRanksByDate(date);
                menuRanks = ranks.stream().collect(toMap(Vote.Rank::getId, Vote.Rank::getRank));
            } catch (Exception ignored) {
            }
    
            Collection<Resource<Menu.Detailed>> resultContent = new ArrayList<>();
    
            for (Resource<Menu.Detailed> menuResource : content) {
                resultContent.add(fillResource(menuResource, voteMenuId, menuRanks));
            }
    
            Resources<Resource<Menu.Detailed>> resultResources = new Resources<>(resultContent, resource.getLinks());
            resultResources.add(entityLinks.linkFor(User.class).slash("choice").withRel("choice"));
            return resultResources;
        }
    
        private Resource<Menu.Detailed> fillResource(
                Resource<Menu.Detailed> resource,
                final long voteMenuId,
                Map<Long, Integer> menuRanks) {
        
            Menu.Detailed menu = resource.getContent();
            long menuId = menu.getId();

            MenuView menuView = new MenuView(menu)
                    .setChosen(voteMenuId == menuId)
                    .setRank(menuRanks != null? menuRanks.getOrDefault(menuId, 0) : null);
        
            Resource<Menu.Detailed> result = new Resource<>(menuView, resource.getLinks());
        
            result.add(entityLinks.linkForSingleResource(Menu.class, menuId).slash("vote").withRel("vote"));
        
            return result;
        }
    }
}
