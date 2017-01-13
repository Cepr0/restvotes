package restvotes.web.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import restvotes.domain.entity.Menu;
import restvotes.repository.PollRepo;

/**
 * @author Cepro, 2017-01-08
 */
// @Component
public class MenuResourceAssembler extends ResourceAssemblerSupport<Menu, MenuResource> {
    
    private PollRepo pollRepo;
    
    public MenuResourceAssembler() {
        super(Menu.class, MenuResource.class);
        
    }
    
    @Autowired
    public void setPollRepo(PollRepo pollRepo) {
        this.pollRepo = pollRepo;
    }
    
    @Override
    public MenuResource toResource(Menu menu) {
        MenuResource resource = createResourceWithId(menu.getId(), menu);
        // Menu content = resource.getContent();
        // content.setRestaurant(menu.getRestaurant());
        // resource.chosen = false;
        // content.setItems(menu.getItems());
        return resource;
    }
}
