package restvotes.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Restaurant;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.rest.webmvc.ProfileController.getRootPath;

/**
 * @author Cepro, 2017-01-14
 */
@Component
public class LinksHelper {
    
    private static final String POLL = "poll";
    private static final String CURRENT_POLL = "currentPoll";
    private static final String MENU = "menu";
    private static final String RESTAURANT = "restaurant";
    private static final String VOTE = "vote";
    private static final String WINNER = "winner";
    private static final String MENUS = "menus";
    private static final String PROFILE = "profile";
    private static final String POLLS = "polls";
    private static final String SEARCH = "search";
    private static final String USER_PROFILE = "userProfile";
    private static final String SLASH = "/";
    private static final String CURRENT = "current";
    
    private static RepositoryEntityLinks links;
    
    private static RepositoryRestConfiguration configuration;
    
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LinksHelper(RepositoryEntityLinks entityLinks, RepositoryRestConfiguration configuration) {
        LinksHelper.links = entityLinks;
        Assert.notNull(configuration, "RepositoryRestConfiguration must not be null!");
        LinksHelper.configuration = configuration;
    }
    
    
    public static Iterable<Link> getMenuLinks(Menu menu, Boolean finished) {
        
        LinkBuilder menuLinkBuilder = links.linkForSingleResource(Menu.class, menu.getId());
        Link restaurantLink = links.linkForSingleResource(Restaurant.class, menu.getRestaurant().getId()).withRel(RESTAURANT);

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
            links.add(LinksHelper.links.linkForSingleResource(Menu.class, chosenMenuId).withRel("userChoice"));
        }
        
        if (winner != null) {
            links.add(LinksHelper.links.linkForSingleResource(Poll.class, pollDate).slash(MENUS).slash(winner).withRel(WINNER));
        }
        
        return links;
    }
    
    public static Link getPollLink(Poll poll) {
        return links.linkForSingleResource(poll).withRel(POLL);
    }
    
    public static Link getPollLink(LocalDate date) {
        return links.linkForSingleResource(Poll.class, date).withRel(POLL);
    }
    
    public static Link getCurrentPollLink() {
        return links.linkFor(Poll.class).slash(CURRENT).withRel(CURRENT_POLL);
    }
    
    public static Link getCurrentPollLink(Poll poll) {
        return links.linkForSingleResource(poll).withRel(CURRENT_POLL);
    }
    
    public static Link getCurrentPollLink(LocalDate date) {
        return links.linkForSingleResource(Poll.class, date).withRel(CURRENT_POLL);
    }
    
    public static Link getPollSelfLink(Poll poll) {
        return links.linkForSingleResource(poll).withSelfRel();
    }
    
    public static Link getPollSelfLink(LocalDate date) {
        return links.linkForSingleResource(Poll.class, date).withSelfRel();
    }
    
    public static Link getRestaurantLink(Restaurant restaurant) {
        return links.linkForSingleResource(restaurant).withRel(RESTAURANT);
    }
    
    public static Link getRestauranMenustLink(Restaurant restaurant) {
        return links.linkForSingleResource(restaurant).slash(MENUS).withRel(MENUS);
    }
    
    public static Link getMenuLink(Menu menu) {
        return links.linkForSingleResource(menu).withRel(MENU);
    }
    
    public static Link getWinnerLink(Poll.Brief poll, Menu winner) {
        return links.linkForSingleResource(Poll.class, poll.getDate()).slash(MENUS).slash(winner).withRel(WINNER);
    }
    
    public static Link getMenuSelfLink(Menu.Detailed menu) {
        return links.linkForSingleResource(Menu.class, menu.getId()).withSelfRel();
    }
    
    public static Link getPollSearchLink() {
        return links.linkFor(Poll.class).slash(SEARCH).withRel(SEARCH);
    }
    
    public static Link getPollProfileLink() {
        // Link link = links.linkFor(Poll.class).withRel(PROFILE);
        // return new Link(link.getHref().replace("/"+ POLLS, "/"+ PROFILE + "/"+ POLLS), PROFILE);
        return new Link(getRootPath(configuration) + SLASH + POLLS, PROFILE);
    }
    
    public static Link getUserProfileLink() {
        return new Link(getBasePath() + SLASH + USER_PROFILE, USER_PROFILE);
    }
    
    private static String getBasePath() {
        BaseUri baseUri = new BaseUri(configuration.getBaseUri());
        return baseUri.getUriComponentsBuilder().build().toString();
    }
}
