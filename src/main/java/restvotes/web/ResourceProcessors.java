package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.*;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Restaurant;
import restvotes.domain.entity.User;

/**
 * @author Cepro, 2016-12-21
 */
//@Component
@RequiredArgsConstructor
public class ResourceProcessors {

    private final @NonNull EntityLinks entityLinks;
    
//    @Component
    public class PollResourceProcessor implements ResourceProcessor<Resource<Poll>> {
        
        @Override
        public Resource<Poll> process(Resource<Poll> resource) {
            resource.getLinks().remove(resource.getLink("poll"));
            return resource;
        }
    }
    
//    @Component
    public class MenuResourceProcessor implements ResourceProcessor<Resource<Menu>> {
        
        @Override
        public Resource<Menu> process(Resource<Menu> resource) {
            resource.getLinks().remove(resource.getLink("menu"));
            
            Menu menu = resource.getContent();
            resource.add(entityLinks.linkForSingleResource(menu).slash("vote").withRel("vote"));
            return resource;
        }
    }

//    @Component
    public class MenuPageResourceProcessor implements ResourceProcessor<PagedResources<Resource<Menu>>> {

        @Override
        public PagedResources<Resource<Menu>> process(PagedResources<Resource<Menu>> resource) {
            return resource;
        }
    }
    
    // @Component
    public class MenuResourcesProcessor implements ResourceProcessor<Resources<Menu>> {
    
        @Override
        public Resources<Menu> process(Resources<Menu> resource) {
            return resource;
        }
    }
    
//    @Component
    public class RestaurantResourceProcessor implements ResourceProcessor<Resource<Restaurant>> {
    
        @Override
        public Resource<Restaurant> process(Resource<Restaurant> resource) {
            resource.getLinks().remove(resource.getLink("restaurant"));
            return resource;
        }
    }
    
//    @Component
    public class UserResourceProcessor implements ResourceProcessor<Resource<User>> {
        
        @Override
        public Resource<User> process(Resource<User> resource) {
            resource.getLinks().remove(resource.getLink("user"));
            User user = resource.getContent();
            resource.add(entityLinks.linkForSingleResource(user).slash("vote").withRel("vote"));
            return resource;
        }
    }
}
