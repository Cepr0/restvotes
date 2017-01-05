package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.*;
import restvotes.domain.entity.Menu;

import java.util.Collection;

/**
 * @author Cepro, 2016-12-21
 */
// @Component
@RequiredArgsConstructor
public class MenuResourceProcessors {

    private final @NonNull EntityLinks entityLinks;
    
    // @Component
    public class MenuResourcesProcessor implements ResourceProcessor<Resources<Resource<Menu.Detailed>>> {

        @Override
        public Resources<Resource<Menu.Detailed>> process(Resources<Resource<Menu.Detailed>> resource) {
            Collection<Resource<Menu.Detailed>> resources = resource.getContent();
            return resource;
        }
    }
    
    // @Component
    public class MenuPageResourceProcessor implements ResourceProcessor<PagedResources<Resource<Menu>>> {
    
        @Override
        public PagedResources<Resource<Menu>> process(PagedResources<Resource<Menu>> resource) {
            Collection<Resource<Menu>> resources = resource.getContent();
            return resource;
        }
    }
    
    // @Component
    public class MenuResourceProcessor implements ResourceProcessor<Resource<Menu>> {
        
        @Override
        public Resource<Menu> process(Resource<Menu> resource) {
            resource.getLinks().remove(resource.getLink("menu"));
            
            Menu menu = resource.getContent();
            // resource.add(entityLinks.linkForSingleResource(menu).slash("vote").withRel("vote"));
            return resource;
        }
    }
}
