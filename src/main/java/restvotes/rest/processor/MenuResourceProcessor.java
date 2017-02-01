package restvotes.rest.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;

/**
 * @author Cepro, 2017-01-22
 */
@Component
@RequiredArgsConstructor
public class MenuResourceProcessor implements ResourceProcessor<Resource<Menu.Detailed>> {
    
    @Override
    public Resource<Menu.Detailed> process(Resource<Menu.Detailed> resource) {

        // resource.add(getMenuSelfLink(resource.getContent()), getRestaurantLink(resource.getContent().getRestaurant()));
        return resource;
    }
}
