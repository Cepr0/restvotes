package restvotes.web;

import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;

/**
 * @author Cepro, 2017-01-04
 */
@Component
public class LinksProcessor implements ResourceProcessor<Resource<?>> {
    
    @Override
    public Resource<?> process(Resource<?> resource) {

        Class<?> type = getResourceType(resource);
    
        if (type == Menu.class) {
            resource.getLinks().remove(resource.getLink("menu"));
            // Menu menu = (Menu) resource.getContent();
            // resource.add(entityLinks.linkForSingleResource(menu).slash("vote").withRel("vote"));
            resource.add(new Link(resource.getId().getHref() + "/vote", "vote"));
        }

        removeEntityLink(resource, type);

        return resource;
    }

    private Class<?> getResourceType(Resource<?> r) {
        PersistentEntityResource resource = (PersistentEntityResource) r;
        ClassTypeInformation<?> typeInfo = ClassTypeInformation.from(resource.getPersistentEntity().getType());
        return typeInfo.getRawTypeInformation().getType();
    }

    private void removeEntityLink(Resource<?> resource, Class<?> type) {
        try {
            String typeString = type.toString();
            String entityName = typeString.substring(typeString.lastIndexOf(".") + 1).toLowerCase();
            resource.getLinks().remove(resource.getLink(entityName));
        } catch (Exception ignored) {
        }
    }
}
