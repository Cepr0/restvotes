package restvotes.web.processor;

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
    
    @Override
    public Resource<Restaurant> process(Resource<Restaurant> resource) {
        resource.add(LinksHelper.getRestauranMenustLink(resource.getContent()));
        return resource;
    }
}
