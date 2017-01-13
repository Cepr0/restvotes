package restvotes.web.resource;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import restvotes.domain.entity.Menu;

/**
 * @author Cepro, 2017-01-09
 */
// @Component
public class MenuDetailedResourceAssembler extends ResourceAssemblerSupport<Menu.Detailed, MenuDetailedResource> {
    
    public MenuDetailedResourceAssembler() {
        super(Menu.Detailed.class, MenuDetailedResource.class);
    }
    
    @Override
    public MenuDetailedResource toResource(Menu.Detailed menu) {
        MenuDetailedResource resource = createResourceWithId(menu.getId(), menu);
        
        resource.setChosen(true);
        resource.setRank(1);
        
        return resource;
    }
}
