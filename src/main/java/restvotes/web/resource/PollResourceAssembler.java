package restvotes.web.resource;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.MenuItem;
import restvotes.domain.entity.Poll;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cepro, 2017-01-07
 */
@Component
public class PollResourceAssembler extends ResourceAssemblerSupport<Poll, PollResource> {
    
    public PollResourceAssembler() {
        
        super(Poll.class, PollResource.class);
    }
    
    @Override
    public PollResource toResource(Poll poll) {
    
        PollResource pollResource = createResourceWithId(poll.getDate(), poll);
        pollResource.date = poll.getDate();
        pollResource.finished = poll.isFinished();
    
        List<MenuResource> menuResources = new ArrayList<>();
    
        for (Menu menu : poll.getMenus()) {
            MenuResource menuResource = new MenuResource();
        
            menuResource.price = menu.getPrice();
            menuResource.restaurant = new Resource<>(menu.getRestaurant());
        
            List<Resource<MenuItem>> itemResources = new ArrayList<>();
            menu.getItems().forEach(item -> itemResources.add(new Resource<>(item)));
            menuResource.items = new Resources<>(itemResources);
        
            menuResources.add(menuResource);
        }
    
        pollResource.menus = new Resources<>(menuResources);
        return pollResource;
    }
}