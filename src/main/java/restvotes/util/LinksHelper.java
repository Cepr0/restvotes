package restvotes.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Restaurant;
import restvotes.web.view.MenuView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Cepro, 2017-01-14
 */
@Component
public class LinksHelper {
    
    public static final String PROJECTION_DETAILED = "/?projection=detailed";
    public static final String POLL = "poll";
    public static final String CURRENT_POLL = "currentPoll";
    public static final String MENU = "menu";
    public static final String RESTAURANT = "restaurant";
    public static final String VOTE = "vote";
    public static final String WINNER = "winner";
    private static final String MENUS = "menus";
    
    private static RepositoryEntityLinks LINKS;
    
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LinksHelper(RepositoryEntityLinks entityLinks) {
        LINKS = entityLinks;
    }
    
    
    public static Iterable<Link> getMenuViewLinks(MenuView menuView, Boolean finished) {
        
        LinkBuilder menuLinkBuilder = LINKS.linkForSingleResource(Menu.class, menuView.getId());
        Link restaurantLink = LINKS.linkForSingleResource(Restaurant.class, menuView.getRestaurant().getId()).withRel(RESTAURANT);

        if (finished != null && !finished) {
            return Arrays.asList(
                    menuLinkBuilder.withSelfRel(),
                    restaurantLink,
                    menuLinkBuilder.slash(VOTE).withRel(VOTE) // Only if Poll is not finished
            );
        } else {
            return Arrays.asList(
                    menuLinkBuilder.withSelfRel(),
                    restaurantLink
            );
        }
    }
    
    public static Iterable<Link> getPollViewLinks(LocalDate pollDate, Long chosenMenuId, Menu winner) {
        
        List<Link> links = new ArrayList<>();
        
        links.add(getPollSelfLink(pollDate));
        
        if (chosenMenuId != null) {
            links.add(LINKS.linkForSingleResource(Menu.class, chosenMenuId).withRel("userChoice"));
        }
        
        if (winner != null) {
            links.add(LINKS.linkForSingleResource(Poll.class, pollDate).slash(MENUS).slash(winner).withRel(WINNER));
        }
        
        return links;
    }
    
    public static Link getPollLink(Poll poll) {
        return LINKS.linkForSingleResource(poll).withRel(POLL);
    }
    
    public static Link getPollLink(LocalDate date) {
        return LINKS.linkForSingleResource(Poll.class, date).withRel(POLL);
    }
    
    public static Link getCurrentPollLink(Poll poll) {
        return LINKS.linkForSingleResource(poll).withRel(CURRENT_POLL);
    }
    
    public static Link getCurrentPollLink(LocalDate date) {
        return LINKS.linkForSingleResource(Poll.class, date).withRel(CURRENT_POLL);
    }
    
    public static Link getPollSelfLink(Poll poll) {
        return LINKS.linkForSingleResource(poll).withSelfRel();
    }
    
    public static Link getPollSelfLink(LocalDate date) {
        return LINKS.linkForSingleResource(Poll.class, date).withSelfRel();
    }
    
    public static Link getRestaurantLink(Restaurant restaurant) {
        return LINKS.linkForSingleResource(restaurant).withRel(RESTAURANT);
    }
    
    public static Link getMenuLink(Menu menu) {
        return LINKS.linkForSingleResource(menu).withRel(MENU);
    }
    
    public static Link getWinnerLink(Poll.Brief poll, Menu winner) {
        return LINKS.linkForSingleResource(Poll.class, poll.getDate()).slash(MENUS).slash(winner).withRel(WINNER);
    }
}
