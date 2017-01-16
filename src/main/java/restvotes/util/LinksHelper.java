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
    
    private static final String PROJECTION_DETAILED = "/?projection=detailed";
    private static final String POLL = "poll";
    private static final String CURRENT_POLL = "currentPoll";
    private static final String MENU = "menu";
    private static final String RESTAURANT = "restaurant";
    private static final String VOTE = "vote";
    private static final String WINNER = "winner";
    
    private static RepositoryEntityLinks LINKS;
    
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LinksHelper(RepositoryEntityLinks entityLinks) {
        LINKS = entityLinks;
    }
    
    
    public static Iterable<Link> getMenuViewLinks(MenuView menuView) {
        
        LinkBuilder menuLinkBuilder = LINKS.linkForSingleResource(Menu.class, menuView.getId());
        Link selfLink = menuLinkBuilder.withSelfRel();
        Link voteLink = menuLinkBuilder.slash(VOTE).withRel(VOTE);
        Link restaurantLink = LINKS.linkForSingleResource(Restaurant.class, menuView.getRestaurant().getId()).withRel(RESTAURANT);
        
        return Arrays.asList(selfLink, restaurantLink, voteLink);
    }
    
    public static Iterable<Link> getPollViewLinks(LocalDate pollDate, Long chosenMenuId) {
        
        List<Link> links = new ArrayList<>();
        
        links.add(getPollSelfLink(pollDate));
        
        if (chosenMenuId != null) {
            links.add(LINKS.linkForSingleResource(Menu.class, chosenMenuId).withRel("userChoice"));
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
    
    public static Link getWinnerLink(Menu winner) {
        return LINKS.linkForSingleResource(winner).withRel(WINNER);
    }
}
