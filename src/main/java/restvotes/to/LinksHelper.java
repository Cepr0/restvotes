package restvotes.to;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Restaurant;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Cepro, 2017-01-14
 */
@Component
public class LinksHelper {
    
    private static RepositoryEntityLinks LINKS;
    
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LinksHelper(RepositoryEntityLinks entityLinks) {
        LINKS = entityLinks;
    }
    
    
    public static Iterable<Link> getMenuViewLinks(MenuView menuView) {
    
        LinkBuilder menuLinkBuilder = LINKS.linkForSingleResource(Menu.class, menuView.getId());
        Link selfLink = menuLinkBuilder.withSelfRel();
        Link voteLink = menuLinkBuilder.slash("vote").withRel("vote");
        Link restaurantLink = LINKS.linkForSingleResource(Restaurant.class, menuView.getRestaurant().getId()).withRel("restaurant");
        
        return Arrays.asList(selfLink, restaurantLink, voteLink);
    }
    
    public static Iterable<Link> getPollViewLinks(Poll.Detailed pollView, Long chosenMenuId) {
    
        List<Link> links = new ArrayList<>();
                
        links.add(getPollLink(pollView.getDate()).withSelfRel());
        
        if (chosenMenuId != null) {
            links.add(LINKS.linkForSingleResource(Menu.class, chosenMenuId).withRel("userChoice"));
        }
        
        return links;
    }
    
    public static LinkBuilder getPollLink(LocalDate date) {
        return LINKS.linkForSingleResource(Poll.class, date).slash("/?projection=detailed");
    }
}
