package restvotes.rest.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Restaurant;
import restvotes.rest.controller.RestaurantController;
import restvotes.util.LinksHelper;

/**
 * Resource processor of single {@link Restaurant}
 * <p>Adds link to its {@link Menu} list which is handled in {@link RestaurantController}</p>
 *
 * @author Cepro, 2017-01-22
 * @see RestaurantController
 */
@Component
@RequiredArgsConstructor
public class RestaurantResourceProcessor implements ResourceProcessor<Resource<Restaurant>> {
    
    private final @NonNull LinksHelper links;
        
    @Override
    public Resource<Restaurant> process(Resource<Restaurant> resource) {
        resource.add(links.getRestaurantMenusLink(resource.getContent()));
        return resource;
    }
}
