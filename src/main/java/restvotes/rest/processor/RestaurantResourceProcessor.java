package restvotes.rest.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Restaurant;
import restvotes.util.LinksHelper;

/**
 * @author Cepro, 2017-01-22
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
