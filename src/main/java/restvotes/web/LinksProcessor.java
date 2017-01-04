package restvotes.web;

import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Restaurant;
import restvotes.domain.entity.User;

/**
 * @author Cepro, 2017-01-04
 */
@Component
public class LinksProcessor implements ResourceProcessor<Resource<?>> {
    
    @Override
    public Resource<?> process(Resource<?> resource) {
    
        ClassTypeInformation<?> typeInfo = ClassTypeInformation.from(((PersistentEntityResource) resource).getPersistentEntity().getType());
        Class<?> type = typeInfo.getRawTypeInformation().getType();
    
        if (type == Menu.class) {
            resource.getLinks().remove(resource.getLink("menu"));
            // Menu menu = (Menu) resource.getContent();
            // resource.add(entityLinks.linkForSingleResource(menu).slash("vote").withRel("vote"));
            resource.add(new Link(resource.getId().getHref() + "/vote", "vote"));
        }
    
        if (type == Poll.class) {
            resource.getLinks().remove(resource.getLink("poll"));
        }
    
        if (type == Restaurant.class) {
            resource.getLinks().remove(resource.getLink("restaurant"));
        }
    
        if (type == User.class) {
            resource.getLinks().remove(resource.getLink("user"));
        }
    
        return resource;
    }
}
