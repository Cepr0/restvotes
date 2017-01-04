package restvotes.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;

/**
 * @author Cepro, 2017-01-04
 */
@Component
@RequiredArgsConstructor
public class MenuResourceProcessors {

    private final @NonNull EntityLinks entityLinks;

    @Bean
    public ResourceProcessor<Resource<?>> resourcesProcessor() {

        return new ResourceProcessor<Resource<?>>() {

            @Override
            public Resource<?> process(Resource<?> resource) {

                String entityType = ((PersistentEntityResource) resource).getPersistentEntity().getName();
                if (entityType.endsWith(".Menu") || entityType.endsWith(".User")) {

//                    List<Link> links = resource.getLinks();
//                    String pollsIdMenuLink = links.stream()
//                            .map(Link::toString)
//                            .filter(s -> s.matches("polls\\/\\d{4}-\\d{2}-\\d{2}\\/menus"))
//                            .findFirst()
//                            .orElse(null);
//
//                    if(pollsIdMenuLink != null) {
//
//                    }
                    resource.add(new Link(resource.getId().getHref() + "/vote", "vote"));
                }
                return resource;
            }
        };
    }

//    @Bean
    public ResourceProcessor<Resource<Menu>> menuResourceProcessor() {

        return new ResourceProcessor<Resource<Menu>>() {

            @Override
            public Resource<Menu> process(Resource<Menu> resource) {
                // resource.getLinks().remove(resource.getLink("menu"));
                //
                // Menu menu = resource.getContent();
                // resource.add(entityLinks.linkForSingleResource(menu).slash("vote").withRel("vote"));
                return resource;
            }
        };
    }

//    @Bean
    public ResourceProcessor<Resources<Menu>> menuResourcesProcessor() {

        return new ResourceProcessor<Resources<Menu>>() {

            @Override
            public Resources<Menu> process(Resources<Menu> resource) {
                return resource;
            }
        };
    }

//    @Bean
    public ResourceProcessor<PagedResources<Menu>> menuPagedResourcesProcessor() {

        return new ResourceProcessor<PagedResources<Menu>>() {

            @Override
            public PagedResources<Menu> process(PagedResources<Menu> resource) {
                return resource;
            }
        };
    }
}
